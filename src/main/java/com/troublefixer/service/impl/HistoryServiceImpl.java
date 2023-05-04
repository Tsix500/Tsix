package com.troublefixer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.troublefixer.mapper.HistoryMapper;
import com.troublefixer.service.DefaultService;
import com.troublefixer.vo.LoginUserVo;
import com.troublefixer.vo.Result;
import com.troublefixer.pojo.History;
import com.troublefixer.service.HistoryService;
import com.troublefixer.utils.UserThreadLocal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.troublefixer.vo.ErrorCode.SYSTEM_ERROR;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryMapper historyMapper;

    private final DefaultService defaultService;

    @Value("${adminid}")
    private Long AdminId;

    @Override
    public Result getHistories(String modelName) {
        LoginUserVo userVo = UserThreadLocal.get();
        Long userId = userVo.getUserId();
        LambdaQueryWrapper<History> lqw = new LambdaQueryWrapper<>();
        //添加查询条件
        if(modelName!=null && StringUtils.isNotBlank(modelName.trim())){
            lqw.like(History::getModelName, modelName);
        }
        lqw.eq(History::getUserId, userId);
        lqw.select(History::getModelId,History::getModelName,History::getModelPath,History::getModelPoint);
        lqw.orderByAsc(History::getModelId);
        List<History> histories = historyMapper.selectList(lqw);

        //普通用户包装默认模型
        History defaultModel = defaultService.packModel(defaultService.getDefaultModel());
        histories.add(0, defaultModel);
        return Result.success(histories);
    }

    @Override
    public Result removeHistory(Integer modelId) {
        historyMapper.deleteById(modelId);
        return Result.success(null);
    }

    @Override
    public Result addHistory(History history) {
        if(history == null){
            return Result.fail(SYSTEM_ERROR.getCode(),SYSTEM_ERROR.getMes());
        }
        history.setUserId(UserThreadLocal.get().getUserId());
        historyMapper.insert(history);
        return Result.success(null,"训练模型成功，可在历史记录中查看！");
    }

    @Override
    public String findPath(Integer modelId) {
        return historyMapper.findPathById(modelId);
    }

    @Override
    public Result getSysModels(String modelName) {
        LambdaQueryWrapper<History> lqw = new LambdaQueryWrapper<>();
        //添加查询条件
        if(modelName != null && StringUtils.isNotBlank(modelName.trim())){
            lqw.like(History::getModelName, modelName);
        }

        //获得默认模型
        History defaultModel = defaultService.getDefaultModel();
        lqw.ne(History::getModelId, defaultModel.getModelId());
        lqw.eq(History::getUserId, AdminId);
        lqw.select(History::getModelId,History::getModelName,History::getModelPath,History::getModelPoint);
        lqw.orderByAsc(History::getModelId);
        List<History> histories = historyMapper.selectList(lqw);
        //放入默认模型
        histories.add(0, defaultModel);
        return Result.success(histories);
    }
}
