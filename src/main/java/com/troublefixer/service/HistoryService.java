package com.troublefixer.service;

import com.troublefixer.pojo.History;
import com.troublefixer.vo.Result;

public interface HistoryService {
    Result getHistories(String modelName);

    Result removeHistory(Integer modelId);

    Result addHistory(History history);

    String findPath(Integer modelId);

    Result getSysModels(String modelName);
}
