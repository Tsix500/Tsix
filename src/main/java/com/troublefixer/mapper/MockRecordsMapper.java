package com.troublefixer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.troublefixer.pojo.MockRecords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MockRecordsMapper extends BaseMapper<MockRecords> {
    void addListWithId(@Param("list") List<MockRecords> list, @Param("mockId") Long mockId);
}
