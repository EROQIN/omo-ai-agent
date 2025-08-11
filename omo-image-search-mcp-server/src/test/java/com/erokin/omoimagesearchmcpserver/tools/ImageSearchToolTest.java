package com.erokin.omoimagesearchmcpserver.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageSearchToolTest {

    @Test
    void searchImage() {
        ImageSearchTool imageSearchTool = new ImageSearchTool();
        String result = imageSearchTool.searchImage("cat");
        assertNotNull(result);
    }

    @Test
    void searchMediumImages() {
        ImageSearchTool imageSearchTool = new ImageSearchTool();
        String result = imageSearchTool.searchMediumImages("cat").get(0);
        assertNotNull(result);
    }
}