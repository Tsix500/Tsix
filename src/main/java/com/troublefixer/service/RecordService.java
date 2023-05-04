package com.troublefixer.service;

import com.troublefixer.vo.Result;

public interface RecordService {

    Result deleteRecords(Long mockId);

    Result getRecordsPage(Long mockId, Integer integer, Integer pagenum);

    Result getRecordsResult(Long mockId, Integer pagesize);

    Result getAllStatistics(Integer pagenum, Integer pagesize);

    Result getRecordsByName(String mockName);
}
