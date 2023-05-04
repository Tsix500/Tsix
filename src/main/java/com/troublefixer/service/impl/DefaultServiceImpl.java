package com.troublefixer.service.impl;

import com.troublefixer.mapper.DefaultModelMapper;
import com.troublefixer.mapper.HistoryMapper;
import com.troublefixer.pojo.History;
import com.troublefixer.service.DefaultService;
import com.troublefixer.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DefaultServiceImpl implements DefaultService {

    private final DefaultModelMapper modelMapper;

    private final HistoryMapper historyMapper;

    public History getDefaultModel(){
        Integer id = modelMapper.selectId();
        History history = historyMapper.selectById(id);
        return history;
    }

    //给路径不给id
    public History packModel(History history){
        history.setModelId(-1);
        history.setModelName("默认模型");
        history.setUserId(null);
        return history;
    }

    @Override
    public Result changeModel(Integer modelId) {
        modelMapper.updateId(modelId);
        return Result.success(null);
    }
}
