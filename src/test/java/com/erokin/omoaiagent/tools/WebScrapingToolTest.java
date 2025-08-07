package com.erokin.omoaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebScrapingToolTest {

    @Test
    void scrapeWebPage() {
        WebScrapingTool tool = new WebScrapingTool();
        String result = tool.scrapeWebPage("https://www.example.com");
        assertNotNull(result);
    }
}