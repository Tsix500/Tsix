package com.troublefixer.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("defaultmodel")
public class DefaultModel {

    @TableId
    Integer defaultModelId;

}
