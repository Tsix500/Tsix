package com.troublefixer.service.impl;

import com.troublefixer.mapper.MockRecordsMapper;
import com.troublefixer.mapper.MockStatisticsMapper;
import com.troublefixer.pojo.MockStatistics;
import com.troublefixer.service.MockService;
import com.troublefixer.utils.UserThreadLocal;
import com.troublefixer.vo.*;
import com.troublefixer.pojo.MockRecords;
import com.troublefixer.vo.params.RecordsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.troublefixer.vo.ErrorCode.SYSTEM_ERROR;

@Service
@RequiredArgsConstructor
public class MockServiceImpl implements MockService {

    private final MockStatisticsMapper statisticsMapper;

    private final MockRecordsMapper recordsMapper;

    @Override
//    @Transactional
    public Result saveAndReturn(RecordsResult result, String mockName, Integer pagesize) {
        if(result == null){
            return Result.fail(SYSTEM_ERROR.getCode(),SYSTEM_ERROR.getMes());
        }
        LoginUserVo userVo = UserThreadLocal.get();
        MockStatistics statistics = result.getStatistics();
        List<MockRecords> records = result.getRecords();

        //补全统计结果的信息。设置用户id
        statistics.setUserId(userVo.getUserId());
        Date date = new Date();//获取当前时间
        //设置测试时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(date);
        statistics.setMockDate(now);
        //设置测试名称
        statistics.setMockName(mockName);
        //添加并获取数据库生成的mock_id
        statisticsMapper.insert(statistics);
        Long mockId  = statistics.getMockId();
        System.out.println(mockId);
        statistics.setMockId(mockId);

        //对每一个记录添加上mock_id列插入
        recordsMapper.addListWithId(records, mockId);
        //返回第一页数据
        List<MockRecords> list = records.subList(0, pagesize);
        //更新数据
        Integer total = statistics.getTotal();
        PageVo<MockRecords> page = new PageVo<>();
        page.setTotal(statistics.getTotal());
        //获得总页数
        page.setMaxpage((total + pagesize - 1)/pagesize);
        page.setRecords(list);

        RecordsVo recordsVo = new RecordsVo();
        recordsVo.setStatistics(statistics);
        recordsVo.setPageCurrent(page);
        return Result.success(recordsVo);
    }
}
