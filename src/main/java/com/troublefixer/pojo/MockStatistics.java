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
@TableName("mockstatistics")
public class MockStatistics implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long mockId;

    private String mockName;

    private Long userId;

    private Integer errorOne;

    private Integer errorTwo;

    private Integer errorThree;

    private Integer errorFour;

    private Integer errorFive;

    private Integer errorSix;

    private Integer normal;

    //测试日期（2023-01-11 11:20:31)
    private String mockDate;

    private Integer total;

    @Override
    public String toString() {
        return "统计数据：" +
                "故障一：" + errorOne +
                "  故障二：" + errorTwo +
                "  故障三：" + errorThree +
                "  故障四：" + errorFour +
                "  故障五：" + errorFive +
                "  故障六：" + errorSix +
                "  正常：" + normal +
                "  测试时间：" + mockDate
                ;
    }
}
