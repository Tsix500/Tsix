package com.troublefixer.controller;

import com.troublefixer.service.DefaultService;
import com.troublefixer.service.HistoryService;
import com.troublefixer.utils.UserThreadLocal;
import com.troublefixer.vo.Result;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

import static com.troublefixer.vo.ErrorCode.SYSTEM_ERROR;

@RestController
@RequestMapping("history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    private final RestTemplate restTemplate;

    @Value("${url.prefix}")
    private String prefix;

    @Value("${url.download}")
    private String downloadUrl;

    @Value("${url.remove}")
    private String removeUrl;

    @GetMapping("/all/{modelName}")
    public Result getHistories(@PathVariable(required = false) String modelName){
        return historyService.getHistories(modelName);
    }

    @DeleteMapping("/{modelId}")
    public Result removeHistory(@PathVariable Integer modelId){
        String modelPath = historyService.findPath(modelId);
        //路径为空，直接删除
        if(modelPath != null && StringUtils.isBlank(modelPath)){
            //将路径传给后端
            ResponseEntity<Boolean> forEntity = restTemplate.getForEntity(prefix + removeUrl, Boolean.class, modelPath);
            boolean result = forEntity.getBody();
            if(!result){
                return Result.fail(SYSTEM_ERROR.getCode(),SYSTEM_ERROR.getMes());
            }
        }
        return historyService.removeHistory(modelId);
    }

    @GetMapping("download/{modelId}")
    public void download(@PathVariable Integer modelId, HttpServletResponse response){
        try {
            String modelPath = historyService.findPath(modelId);
            if(StringUtil.isNullOrEmpty(modelPath)){
                return;
            }
            URL url = new URL(prefix +downloadUrl + "?modelPath=" + modelPath);
            //创建代理请求
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            //添加返回头信息
            response.addHeader("Content-Disposition",
                    "attachmen;filename=" + URLEncoder.encode(UUID.randomUUID() + ".h5", "utf-8"));
            response.setContentType("application/octet-stream");

            // 将代理请求的响应数据流复制到原始响应中
            response.setStatus(connection.getResponseCode());
            response.setContentType(connection.getContentType());
            InputStream proxyInputStream = connection.getInputStream();
            OutputStream proxyOutputStream = response.getOutputStream();
            byte[] proxyBuffer = new byte[1024];
            int proxyLen;
            while ((proxyLen = proxyInputStream.read(proxyBuffer)) != -1) {
                proxyOutputStream.write(proxyBuffer, 0, proxyLen);
            }
            proxyInputStream.close();
            connection.disconnect();
            proxyOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}