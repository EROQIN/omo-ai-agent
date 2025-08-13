package com.erokin.omoaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.erokin.omoaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 模版设计模式，定义Agent的执行流程
 * 提供状态转换，内存管理和基于步骤的执行循环的基础功能
 */
@Data
@Slf4j
public abstract class BaseAgent {
    //核心属性
    private String name;

    //提示词
    private String systemPrompt;
    private String nextStepPrompt;

    //状态
    private AgentState state = AgentState.IDLE;

    //执行步骤控制
    private int currentStep = 0;
    private int maxSteps = 10;

    //LLM大模型
    private ChatClient chatClient;

    //Memory 记忆(需要自主维护会话上下文)
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt){
        //状态校验
        if(this.state != AgentState.IDLE){
            throw new RuntimeException("Agent is not idle");
        }
        //校验提示词
        if(StrUtil.isBlankIfStr(userPrompt)){
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }
        //执行
        this.state = AgentState.RUNNING;

        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));

        //定义一个结果列表
        List<String> results = new ArrayList<>();

        try {
            // 执行循环
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step {}/{}", stepNumber, maxSteps);
                //单步执行
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);

            }


            //检查是否超出步骤限制
            if(currentStep >= maxSteps){
                this.state = AgentState.FINISHED;
                results.add("Terminated: Reach max steps (" + maxSteps + ")" );
            }
            return String.join("\n", results);
        } catch (Exception e) {
            this.state = AgentState.ERROR;
            log.error("Error executing agent: ", e);
            results.add("Error: " + e.getMessage());
            return "执行错误："+ e.getMessage();

        } finally {
            //清理资源
            this.cleanup();
        }
    }


    /**
     * 运行代理(流式输出)
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public SseEmitter runStream(String userPrompt){
        //创建一个超时时长较长的SSE
        SseEmitter sseEmitter = new SseEmitter(300000L);

        CompletableFuture.runAsync(() -> {
            //状态校验
            try {
                if(this.state != AgentState.IDLE){
                    sseEmitter.send("Error: Agent is not idle");
                    sseEmitter.completeWithError(new RuntimeException("Agent is not idle"));
                    return;
                }
                //校验提示词
                if(StrUtil.isBlankIfStr(userPrompt)){
                        sseEmitter.send("Error: Cannot run agent with empty user prompt");
                        sseEmitter.completeWithError(new RuntimeException("Cannot run agent with empty user prompt"));
                }
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
            //执行
            this.state = AgentState.RUNNING;

            // 记录消息上下文
            messageList.add(new UserMessage(userPrompt));

            //定义一个结果列表
            List<String> results = new ArrayList<>();

            try {
                // 执行循环
                for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    log.info("Executing step {}/{}", stepNumber, maxSteps);
                    //单步执行
                    String stepResult = step();
                    String result = "Step " + stepNumber + ": " + stepResult;
                    results.add(result);
                    //输出每一步的结果到SSE
                    sseEmitter.send(result);

                }


                //检查是否超出步骤限制
                if(currentStep >= maxSteps){
                    this.state = AgentState.FINISHED;
                    results.add("Terminated: Reach max steps (" + maxSteps + ")" );
                    sseEmitter.send("Terminated: Reach max steps (" + maxSteps + ")" );
                }
                //正常完成
                sseEmitter.complete();
            } catch (Exception e) {
                this.state = AgentState.ERROR;
                log.error("Error executing agent: ", e);
                try {
                    sseEmitter.send("Error: " + e.getMessage());
                    sseEmitter.completeWithError(e);
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
                results.add("Error: " + e.getMessage());
            } finally {
                //清理资源
                this.cleanup();
            } });

        //设置超时回调
        sseEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timeout");
        });

        sseEmitter.onCompletion(() -> {
            if(state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });
        return sseEmitter;

    }
    /**
     * 定义单个步骤
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup(){
        //子类可以重写该方法来清理资源
    }




}
