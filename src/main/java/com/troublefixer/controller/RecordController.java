package com.troublefixer.controller;

import ch.qos.logback.core.util.FileUtil;
import com.troublefixer.service.FileService;
import com.troublefixer.service.MockService;
import com.troublefixer.service.RecordService;
import com.troublefixer.vo.Result;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import static com.troublefixer.vo.ErrorCode.SYSTEM_ERROR;

@RestController
@RequestMapping("records")
@RequiredArgsConstructor
public class RecordController {

    private final FileService fileService;

    private final RecordService recordService;

    private final String relativePath = "/recordsfile/";

    @DeleteMapping("/{mockId}")
    public Result deleteRecords(@PathVariable Long mockId) {
        return recordService.deleteRecords(mockId);
    }

    @GetMapping("/statistics/{pagenum}/{pagesize}")
    public Result getAllRecords(@PathVariable Integer pagenum,@PathVariable Integer pagesize) {
        return recordService.getAllStatistics(pagenum, pagesize);
    }

    @GetMapping("/byname/{mockName}")
    public Result getRecordsByName(@PathVariable String mockName) {
        return recordService.getRecordsByName(mockName);
    }

    @GetMapping("/{mockId}/{pagesize}")
    public Result getRecordsResult(@PathVariable Long mockId,@PathVariable Integer pagesize){
        return recordService.getRecordsResult(mockId, pagesize);
    }
    @GetMapping("/{mockId}/{pagenum}/{pagesize}")
    public Result getRecordsPage(@PathVariable Long mockId,@PathVariable Integer pagenum,@PathVariable Integer pagesize){
        return recordService.getRecordsPage(mockId,pagenum,pagesize);
    }

    @GetMapping("download/{mockId}")
    public Result download(@PathVariable Long mockId, HttpServletRequest request, HttpServletResponse response) {
        String path = request.getContextPath() + relativePath;
        //生成文件
        String fileName = fileService.generateFile(mockId, path);
        File file = new File(path + fileName);
        if(!file.isFile() || !file.exists()) {
            return Result.fail(SYSTEM_ERROR.getCode(), SYSTEM_ERROR.getMes());
        }
        try (ServletOutputStream os = response.getOutputStream();
             FileInputStream fis = new FileInputStream(file);) {
            response.addHeader("Content-Disposition",
                    "attachmen;filename=" + URLEncoder.encode(fileName, "utf-8"));
            response.setContentType("application/octet-stream");
            byte[] bytes = new byte[1024];
            int n = 0;
            while ((n = fis.read(bytes)) != -1) {
                os.write(bytes,0, n);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.deleteOnExit();
        return null;
    }
}

