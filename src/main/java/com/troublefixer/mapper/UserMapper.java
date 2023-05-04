package com.troublefixer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.troublefixer.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    Integer selectRoleById(@Param("userId") Long userId);
}
