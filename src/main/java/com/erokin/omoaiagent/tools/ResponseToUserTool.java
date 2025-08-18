package com.erokin.omoaiagent.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

@Slf4j
@Component
public class ResponseToUserTool {

    @Tool(description = "Send a feedback/summary to the user when you have enough information")
    public String responseToUser(
            @ToolParam(description = "The response to user") String response) {
        return "\n_________\n\n"+response+"\n_________\n\n";
    }
}