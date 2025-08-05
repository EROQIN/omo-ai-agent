package com.erokin.omoaiagent.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义基于token的切词器
 */
@Component
class MyTokenTextSplitter {
    public List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(documents);
    }

    public List<Document> splitCustomized(List<Document> documents) {
        // 创建一个自定义配置的TokenTextSplitter实例
        // 参数说明：
        // - 200: 每个文本块的最大token数量
        // - 100: 相邻文本块之间的重叠token数量
        // - 10: 最小文本块大小（token数）
        // - 5000: 最大总token数限制
        // - true: 是否保留原始文档的元数据
        TokenTextSplitter splitter = new TokenTextSplitter(200, 100, 10, 5000, true);
        return splitter.apply(documents);
    }
}
