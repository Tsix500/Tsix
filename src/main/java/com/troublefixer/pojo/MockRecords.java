package com.troublefixer.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("mockrecords")
public class MockRecords implements Serializable {

    private Integer errorType;

//    @MppMultiId
//    @TableField("data_id")
    private Integer dataId;

//    @MppMultiId
    @TableId
    private Long mockId;

    @Override
    public String toString() {
        return  dataId + ":" +
                errorType + ","
                ;
    }
}
