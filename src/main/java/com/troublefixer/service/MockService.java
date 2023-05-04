package com.troublefixer.service;

import com.troublefixer.vo.Result;
import com.troublefixer.vo.params.RecordsResult;

public interface MockService {

    Result saveAndReturn(RecordsResult result, String mockName, Integer pagesize);

}
