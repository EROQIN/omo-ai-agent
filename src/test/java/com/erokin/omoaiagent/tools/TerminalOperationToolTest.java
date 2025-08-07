package com.erokin.omoaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalOperationToolTest {


    @Test
    void executeTerminalCommand() {
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        String result = terminalOperationTool.executeTerminalCommand("ls -l");
        assertNotNull(result);
    }
}