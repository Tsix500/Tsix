package com.troublefixer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.troublefixer.mapper.MockRecordsMapper;
import com.troublefixer.mapper.MockStatisticsMapper;
import com.troublefixer.pojo.MockRecords;
import com.troublefixer.pojo.MockStatistics;
import com.troublefixer.service.RecordService;
import com.troublefixer.utils.UserThreadLocal;
import com.troublefixer.vo.LoginUserVo;
import com.troublefixer.vo.PageVo;
import com.troublefixer.vo.RecordsVo;
import com.troublefixer.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final MockStatisticsMapper statisticsMapper;

    private final MockRecordsMapper recordsMapper;

    @Override
    public Result deleteRecords(Long mockId) {
        statisticsMapper.deleteById(mockId);
        LambdaQueryWrapper<MockRecords> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MockRecords::getMockId,mockId);
        recordsMapper.delete(lqw);
        return Result.success(null,"删除成功");
    }



    @Override
    public Result getAllStatistics(Integer pagenum, Integer pagesize) {
        Page<MockStatistics> page = new Page<>(pagenum, pagesize);
        LoginUserVo userVo = UserThreadLocal.get();
        LambdaQueryWrapper<MockStatistics> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MockStatistics::getUserId, userVo.getUserId());

        //查询一页数据
        page = statisticsMapper.selectPage(page, lqw);
        //包装页面数据
        PageVo<MockStatistics> pageVo = new PageVo<>();
        pageVo.setTotal((int)page.getTotal());
        pageVo.setMaxpage((int)page.getPages());
        pageVo.setRecords(page.getRecords());
        return Result.success(pageVo);
    }

    @Override
    public Result getRecordsByName(String mockName) {
        LambdaQueryWrapper<MockStatistics> lqw = new LambdaQueryWrapper();
        lqw.eq(MockStatistics::getMockName, mockName);
        MockStatistics statistics = statisticsMapper.selectOne(lqw);
        return Result.success(statistics);
    }

    @Override
    public Result getRecordsPage(Long mockId, Integer pagenum, Integer pagesize) {
        PageVo pageVo = getPageById(mockId, pagenum, pagesize);
        return Result.success(pageVo);
    }

    @Override
    public Result getRecordsResult(Long mockId, Integer pagesize) {
        MockStatistics statistics = statisticsMapper.selectById(mockId);
        PageVo page = getPageById(mockId,1,pagesize);
        RecordsVo recordsVo = new RecordsVo();
        recordsVo.setStatistics(statistics);
        recordsVo.setPageCurrent(page);
        return Result.success(recordsVo);
    }

    /**
     * 通过id获取页面
     * @param mockId 测试id
     * @param pagenum 页面位置
     * @param pagesize 页面大小
     * @return
     */
    private PageVo getPageById(Long mockId, Integer pagenum, Integer pagesize){
        Page<MockRecords> page = new Page<>(pagenum,pagesize);
        LambdaQueryWrapper<MockRecords> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MockRecords::getMockId, mockId);
        page = recordsMapper.selectPage(page, lqw);
        PageVo pageVo = new PageVo();
        pageVo.setTotal(Integer.valueOf((int)page.getTotal()));
        pageVo.setMaxpage(Integer.valueOf((int)page.getPages()));
        pageVo.setRecords(page.getRecords());
        return pageVo;
    }
}
