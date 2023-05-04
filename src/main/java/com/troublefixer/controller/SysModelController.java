package com.troublefixer.controller;

import com.troublefixer.annotations.Privilige;
import com.troublefixer.pojo.History;
import com.troublefixer.service.DefaultService;
import com.troublefixer.service.HistoryService;
import com.troublefixer.utils.FileUtils;
import com.troublefixer.vo.Result;
import com.troublefixer.vo.params.RecordsResult;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.troublefixer.vo.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("sys/models")
public class SysModelController {

    private final RestTemplate  restTemplate;

    private final HistoryService historyService;


    private final DefaultService defaultService;

    @Value("${adminid}")
    private Long AdminId;

    @Value("${url.prefix}")
    private String prefix;

    @Value("${url.change}")
    private String changeUrl;

    @Value("${url.remove}")
    private String removeUrl;

    private final String[] types = {".csv",".txt",".excel"};

    @Value("${url.upload}")
    private String uploadUrl;

    @GetMapping("getall/{modelName}")
    @Privilige(1)
    public Result getSysModels(@PathVariable(required = false) String modelName){
        return historyService.getSysModels(modelName);
    }

    @DeleteMapping("{modelId}")
    @Privilige(1)
    public Result removeSysModel(@PathVariable Integer modelId){
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

    @GetMapping("change/{modelId}")
    @Privilige(1)
    public Result changeModel(@PathVariable Integer modelId){
        //获得模型路径
        String modelPath = historyService.findPath(modelId);
        if(StringUtil.isNullOrEmpty(modelPath)){
            return Result.fail(PARAM_Error.getCode(),PARAM_Error.getMes());
        }
        System.out.println(prefix + changeUrl);
        //将路径传给后端
        ResponseEntity<String> forEntity = restTemplate.getForEntity(prefix + changeUrl, String.class, modelPath);

        String result = forEntity.getBody();
        if(!Boolean.valueOf(result)){
            return Result.fail(SYSTEM_ERROR.getCode(),SYSTEM_ERROR.getMes());
        }
        //修改默认模型记录
        return defaultService.changeModel(modelId);
    }

    @PostMapping("uploadModel/")
    @Privilige(1)
    public Result uploadModel(MultipartFile modelFile,String modelName,Integer modelPoint){
        String originName = modelFile.getOriginalFilename();
        String filetype = StringUtils.substring(originName, originName.lastIndexOf("."));
        //检查文件类型
        if(!FileUtils.checkType(filetype, types)){
            return Result.fail(FILETTPE_ERROR.getCode(),FILETTPE_ERROR.getMes());
        }
        //保存临时文件
        String path = "modelFile/"+ UUID.randomUUID() + filetype;
        File file = FileUtils.saveMultiFile(modelFile, path);
        MultiValueMap<String,Object> multiValueMap = new LinkedMultiValueMap<>();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);
        History history = null;
        try {
            //添加文件参数
            multiValueMap.add("modelfile",new FileSystemResource(file));
            HttpEntity<MultiValueMap<String,Object>> httpEntity = new HttpEntity<>(multiValueMap,header);
            ResponseEntity<History> postForEntity = restTemplate.postForEntity(prefix + uploadUrl, httpEntity, History.class);
            history = postForEntity.getBody();
            if (history == null){
                return Result.fail(SYSTEM_ERROR.getCode(),SYSTEM_ERROR.getMes());
            }
            //将新模型加入系统模型
            history.setModelName(modelName);
            history.setModelPoint(modelPoint);
            history.setUserId(AdminId);
            return historyService.addHistory(history);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //删除临时文件
        if(file.exists()){
            file.delete();
        }
        return Result.fail(SYSTEM_ERROR.getCode(),SYSTEM_ERROR.getMes());
    }
}
