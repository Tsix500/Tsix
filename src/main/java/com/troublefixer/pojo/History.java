package com.troublefixer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("history")//历史模型表
public class History implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer modelId;

    private String modelName;

    //模型所在路径
    private String modelPath;

    //模型评分
    private Integer modelPoint;

    private Long userId;
}
