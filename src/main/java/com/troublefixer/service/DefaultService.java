package com.troublefixer.service;

import com.troublefixer.pojo.History;
import com.troublefixer.vo.Result;

public interface DefaultService {
    History getDefaultModel();

    History packModel(History defaultModel);

    Result changeModel(Integer modelId);
}
