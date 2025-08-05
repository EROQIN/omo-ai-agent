package com.erokin.omoaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

    /**
     * 基于AI的文档元信息增强器添加关键词（为文档补充元信息）
     */
@Component
public class MyKeyWordEnricher {
    @Resource
    private ChatModel dashScopeChatModel;
        public List<Document> enrichDocuments(List<Document> documents){
            KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashScopeChatModel, 5);
            return keywordMetadataEnricher.apply(documents);

        }

}
