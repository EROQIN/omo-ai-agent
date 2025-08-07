package com.erokin.omoaiagent.documentReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WebPageResource implements Resource {
    private final String url;
    private String htmlContent;

    public WebPageResource(String url) {
        this.url = url;
        fetchHtmlContent();
    }

    private void fetchHtmlContent() {
        try {
            // 创建HttpClient
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            // 创建HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .GET()
                    .build();

            // 发送请求并获取响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 检查响应状态码
            if (response.statusCode() == 200) {
                this.htmlContent = response.body();
            } else {
                throw new RuntimeException("Failed to fetch webpage. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error fetching webpage: " + url, e);
        }
    }

    public String getTextFromHtml() {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return "";
        }

        // 使用Jsoup解析HTML
        Document doc = Jsoup.parse(htmlContent);

        // 移除script和style元素
        doc.select("script").remove();
        doc.select("style").remove();

        // 获取主要文本内容
        return doc.body().text();
    }

    @Override
    public boolean exists() {
        return htmlContent != null;
    }

    @Override
    public URL getURL() throws IOException {
        return URI.create(url).toURL();
    }

    @Override
    public URI getURI() throws IOException {
        return URI.create(url);
    }

    @Override
    public File getFile() throws IOException {
        throw new UnsupportedOperationException("WebPageResource does not support getFile()");
    }

    @Override
    public long contentLength() throws IOException {
        return htmlContent != null ? htmlContent.length() : 0;
    }

    @Override
    public long lastModified() throws IOException {
        // 简化实现，返回当前时间
        return System.currentTimeMillis();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        throw new UnsupportedOperationException("WebPageResource does not support createRelative()");
    }

    @Override
    public String getFilename() {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    @Override
    public String getDescription() {
        return "Web page resource from: " + url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (htmlContent != null) {
            return new ByteArrayInputStream(htmlContent.getBytes());
        }
        throw new IOException("No content available");
    }
}
