package com.erokin.omoaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDownloadToolTest {

    @Test
    void downloadResource() {
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        String result = resourceDownloadTool.downloadResource("https://www.baidu.com", "baidu.html");
        assertTrue(result.contains("baidu.html"));

    }
}