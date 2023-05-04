package com.troublefixer.controller;

import com.troublefixer.pojo.History;
import com.troublefixer.service.HistoryService;
import com.troublefixer.service.MockService;
import com.troublefixer.utils.FileUtils;
import com.troublefixer.vo.Result;
import com.troublefixer.vo.params.RecordsResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.troublefixer.vo.ErrorCode.*;

@RestController
@RequestMapping("upload")
@RequiredArgsConstructor
public class MockController {

    private final RestTemplate restTemplate;

    private final MockService mockService;

    private final HistoryService historyService;

    private final String[] types = {".csv",".txt",".excel"};

    @Value("${url.prefix}")
    private String prefix;

    @Value("${url.test}")
    private String testUrl;

    @Value("${url.train}")
    private String trainUrl;

    @PostMapping("forTrain")
    public Result fileForTrain(MultipartFile trainfile, String modelName){
        String originName = trainfile.getOriginalFilename();
        String filetype = StringUtils.substring(originName, originName.lastIndexOf("."));
        //检查文件类型
        if(!FileUtils.checkType(filetype, types)){
            return Result.fail(FILETTPE_ERROR.getCode(),FILETTPE_ERROR.getMes());
        }
        //保存临时文件
        String path = "trainfile/"+ UUID.randomUUID() + filetype;
        File file = FileUtils.saveMultiFile(trainfile, path);
        History history = null;
        //封装请求
        MultiValueMap<String,Object> multiValueMap = new LinkedMultiValueMap<>();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        multiValueMap.add("trainfile",new FileSystemResource(file));
        HttpEntity<MultiValueMap<String,Object>> httpEntity = new HttpEntity<>(multiValueMap,header);

        ResponseEntity<History> postForEntity = restTemplate.postForEntity(prefix + trainUrl, httpEntity, History.class);
        history = postForEntity.getBody();
        //如果返回为空，抛出异常
        if(history == null){
            return Result.fail(SYSTEM_ERROR.getCode(),SYSTEM_ERROR.getMes());
        }
        history.setModelName(modelName);
        if(file.exists()){
            file.delete();
        }
        return historyService.addHistory(history);
    }

    @PostMapping("forTest")
    public Result fileForCheck(MultipartFile testfile, String mockName, String modelPath, Integer pagesize){

        String originName = testfile.getOriginalFilename();
        String filetype = StringUtils.substring(originName, originName.lastIndexOf("."));
        //检查文件类型
        if(!FileUtils.checkType(filetype, types)){
            return Result.fail(FILETTPE_ERROR.getCode(),FILETTPE_ERROR.getMes());
        }
        //保存临时文件
        String path = "testfile/"+ UUID.randomUUID() + filetype;
        File file = FileUtils.saveMultiFile(testfile, path);
        MultiValueMap<String,Object> multiValueMap = new LinkedMultiValueMap<>();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);
        RecordsResult result = null;
        try {

            //添加文件参数
            multiValueMap.add("testfile",new FileSystemResource(file));
            //添加模型文件路径
            multiValueMap.add("modelPath", modelPath);
            HttpEntity<MultiValueMap<String,Object>> httpEntity = new HttpEntity<>(multiValueMap,header);
            ResponseEntity<RecordsResult> postForEntity = restTemplate.postForEntity(prefix + testUrl, httpEntity, RecordsResult.class);
            result = postForEntity.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(file.exists()){
            file.delete();
        }
        return mockService.saveAndReturn(result, mockName, pagesize);
    }

}
