package com.erokin.omoaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "哈吉米.pdf";
        String content = "喵喵喵";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}
