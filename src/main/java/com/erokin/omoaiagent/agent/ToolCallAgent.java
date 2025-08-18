package com.erokin.omoaiagent.agent;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.erokin.omoaiagent.agent.model.AgentState;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存了工具调用信息的响应
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    @Override
    public boolean think() {
        try {
            //1.校验提示词，拼接用户提示词
            if(StrUtil.isNotBlank(getNextStepPrompt())){
                UserMessage userMessage = new UserMessage(getNextStepPrompt());
                getMessageList().add(userMessage);
            }
            //2.调用AI大模型，获取结果
            List<Message> messageList = getMessageList();
            Prompt prompt = new Prompt(messageList, chatOptions);
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            //3.解析调用结果，获取调用工具
            //记录响应，用于等下Act
            toolCallChatResponse = chatResponse; // 修复：确保在think方法中设置toolCallChatResponse
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();

            log.info(getName() + "的思考：" + result);
            log.info(getName() + "选择了" + toolCallList.size() + "个工具来使用");
            String toolCallInfo = toolCallList.stream().map(toolCall -> String.format("工具名称：%s，参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            if(toolCallList.isEmpty()){
                //只有不调用工具时
                getMessageList().add(assistantMessage);
                return false;
            }
            else {
                return true;
            }
        } catch (Exception e) {
            //异常处理
            log.error(getName() + "的思考过程遇到问题：" + e.getMessage());
            getMessageList().add(new AssistantMessage("处理时遇到了错误：" + e.getMessage()));
            return false;
        }
    }

    @Override
    public String act() {
        if(toolCallChatResponse == null || !toolCallChatResponse.hasToolCalls()){
            return "没有工具需要调用";
        }
        //调用工具
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        //记录工具调用结果
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollectionUtil.getLast(toolExecutionResult.conversationHistory());
        //判断是否调用了终止工具
        if(toolResponseMessage.getResponses().stream().anyMatch(response -> response.name().equals("doTerminate"))){
            setState(AgentState.FINISHED);
        }
        //判断是否调用了反馈工具
        if(Objects.equals(toolResponseMessage.getResponses().getLast().name(), "responseToUser")){
            String s = toolResponseMessage.getResponses().getLast().responseData();
            getFeedbackList().add(s);
            getMessageList().add(new UserMessage("反馈消息发送成功，接下来，你可以 `调用其他工具继续任务` 或 `使用doTerminate方法结束会话，等待用户的进一步输入` "));
        }
        String results = toolResponseMessage.getResponses().stream().map(response -> response.name())
                .collect(Collectors.joining("\n"));
        log.info(results);
        return results;
    }
}


