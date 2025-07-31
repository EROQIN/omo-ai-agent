package com.erokin.omoaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 使用Hutool工具类调用阿里云AI服务(HTTP形式)
 */
public class HttpAiInvoke {
    
    public static String callWithHutool() {
        // 构造请求URL
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
        
        // 构造请求体JSON
        JSONObject requestBody = JSONUtil.createObj();
        requestBody.set("model", "qwen-plus");
        
        JSONObject input = JSONUtil.createObj();
        JSONArray messages = JSONUtil.createArray();
        
        JSONObject systemMessage = JSONUtil.createObj();
        systemMessage.set("role", "system");
        systemMessage.set("content", "You are a helpful assistant.");
        
        JSONObject userMessage = JSONUtil.createObj();
        userMessage.set("role", "user");
        userMessage.set("content", "你是谁？");
        
        messages.add(systemMessage);
        messages.add(userMessage);
        input.set("messages", messages);
        requestBody.set("input", input);
        
        JSONObject parameters = JSONUtil.createObj();
        parameters.set("result_format", "message");
        requestBody.set("parameters", parameters);
        
        // 发送POST请求
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + TestApiKey.API_KEY)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute();
        
        // 返回响应体
        return response.body();
    }
    
    public static void main(String[] args) {
        // 使用Hutool调用
        String hutoolResult = callWithHutool();
        System.out.println("Hutool调用结果: " + hutoolResult);
    }
}
