package com.erokin.omoaiagent.controller;

import com.erokin.omoaiagent.agent.OmoManus;
import com.erokin.omoaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashScopeChatModel;


    /**
     * 同步调用AI恋爱大师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message,String chatId){
        return loveApp.doChat(message, chatId);
    }

    /**
     * SSE流式调用AI恋爱大师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSse(String message, String chatId){
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * SSE流式调用AI恋爱大师应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId){
        return loveApp.doChatByStream(message, chatId).map(chunk -> ServerSentEvent.<String>builder().data(chunk).build());
    }

    /**
     * SSE流式调用AI恋爱大师应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse_emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId){
        //创建一个超时时长较长的SSE
        SseEmitter sseEmitter = new SseEmitter(1800000L);
        //获取 Flux 响应式数据流并且直接通过订阅推送给sseEmitter
        loveApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                },sseEmitter::completeWithError,sseEmitter::complete);
        //返回
        return sseEmitter;
    }

    /**
     * SSE流式调用omoManus
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message){
        OmoManus omoManus = new OmoManus(allTools,dashScopeChatModel);
        return omoManus.runStream(message);
    }


}
