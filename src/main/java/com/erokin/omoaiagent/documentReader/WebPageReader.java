package com.erokin.omoaiagent.documentReader;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;

import java.util.List;

public class WebPageReader implements DocumentReader {
    private final WebPageResource webPageResource;

    public WebPageReader(String url) {
        this.webPageResource = new WebPageResource(url);
    }

    public WebPageReader(WebPageResource webPageResource) {
        this.webPageResource = webPageResource;
    }

    @Override
    public List<Document> read() {
        String textContent = webPageResource.getTextFromHtml();
        
        // 创建Document对象
        Document document = new Document(textContent);
        
        // 添加元数据
        try {
            document.getMetadata().put("url", webPageResource.getURI().toString());
            document.getMetadata().put("source", webPageResource.getDescription());
        } catch (Exception e) {
            // 忽略元数据添加异常
        }
        
        return List.of(document);
    }

    @Override
    public List<Document> get() {
        return read();
    }
}
