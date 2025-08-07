package com.erokin.omoaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationToolTest {

    @Test
    void writeFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "test.txt";
        String content = "This is a test file.";
        String result = fileOperationTool.writeFile(fileName, content);
    }

    @Test
    void readFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "test.txt";
        String content = fileOperationTool.readFile(fileName);
        assertEquals("This is a test file.", content);
    }
    @Test
    void detectFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "test.txt";
        String result = fileOperationTool.detectFile(fileName);
        assertEquals("File exist", result);
    }


}