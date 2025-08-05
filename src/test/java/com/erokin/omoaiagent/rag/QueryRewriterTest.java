package com.erokin.omoaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class QueryRewriterTest {
    @Resource
    private ChatModel dashScopeChatModel;
    @Resource
    private QueryRewriter queryRewriter;


    @Test
    void doQueryRewrite() {
        String result = queryRewriter.doQueryRewrite("What is the meaning of life?");
        Assertions.assertNotNull(result);

    }
}