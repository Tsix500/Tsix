package com.troublefixer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.troublefixer.pojo.History;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HistoryMapper extends BaseMapper<History> {
    String findPathById(@Param("modelId") Integer modelId);

}
