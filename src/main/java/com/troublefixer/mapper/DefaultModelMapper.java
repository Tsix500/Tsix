package com.troublefixer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.troublefixer.pojo.DefaultModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DefaultModelMapper extends BaseMapper<DefaultModel> {
    Integer selectId();

    void updateId(@Param("modelId") Integer modelId);

}
