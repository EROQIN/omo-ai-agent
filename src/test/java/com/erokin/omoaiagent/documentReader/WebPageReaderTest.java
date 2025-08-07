package com.erokin.omoaiagent.documentReader;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebPageReaderTest {
    
    private static final String TEST_URL = "https://httpbin.org/html";
    private WebPageReader reader = new WebPageReader(TEST_URL);

    @Test
    void testWebPageReaderRead() {
        // 使用预定义的reader读取文档
        List<Document> documents = reader.read();
        
        // 验证结果
        assertNotNull(documents);
        assertFalse(documents.isEmpty());
        assertNotNull(documents.get(0).getText());
        assertFalse(documents.get(0).getText().isEmpty());
    }
    
    @Test
    void testWebPageReaderGet() {
        // 使用预定义的reader获取文档
        List<Document> documents = reader.get();
        
        // 验证结果
        assertNotNull(documents);
        assertFalse(documents.isEmpty());
        assertNotNull(documents.get(0).getText());
        assertFalse(documents.get(0).getText().isEmpty());
    }
    
    @Test
    void testWebPageResourceTextExtraction() {
        // 创建WebPageResource实例并提取文本
        WebPageResource resource = new WebPageResource(TEST_URL);
        String text = resource.getTextFromHtml();
        
        // 验证结果
        assertNotNull(text);
        assertFalse(text.isEmpty());
        // httpbin.org/html页面应该包含"Herman Melville"文本
        assertTrue(text.contains("Herman Melville"));
    }
}