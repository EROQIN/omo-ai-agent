package com.erokin.omoaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.erokin.omoaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类（提供文件读写功能）
 */
public class FileOperationTool {
    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of file to read", required = true) String fileName){
        String filePath = FILE_DIR + "/" + fileName;
        try{
            return FileUtil.readUtf8String(filePath);
        }
        catch (Exception e){
            return "Error reading file:" + e.getMessage();
        }
    }

    @Tool(description = "Check a file if exist")
    public String detectFile(@ToolParam(description = "Name of file to detect", required = true) String fileName){
        String filePath = FILE_DIR + "/" + fileName;
        try{
            return FileUtil.exist(filePath) ? "File exist" : "File not exist";
        }
        catch (Exception e){
            return "Error reading file:" + e.getMessage();
        }
    }

    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of the file to write", required = true) String fileName,
                          @ToolParam(description = "Content to write to the file", required = true) String content){
        String filePath = FILE_DIR + "/" + fileName;
        try {
            //创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content,filePath);
            return "File written successfully to:" + filePath;
        }
        catch (Exception e) {
            return "Error writing to file: " + e.getMessage();
        }
    }
    /**
     * 返回文件夹下的所有文件名
     */
    @Tool(description = "Returns a list of all files in the directory")
    public String listFiles(){
        return FileUtil.listFileNames(FILE_DIR).toString();

    }

}
