package com.troublefixer.vo;

import com.troublefixer.pojo.MockStatistics;
import com.troublefixer.pojo.MockRecords;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordsVo implements Serializable {

    private MockStatistics statistics;

    private PageVo<MockRecords> pageCurrent;

}
