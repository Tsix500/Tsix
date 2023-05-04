package com.troublefixer.vo.params;

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
public class RecordsResult implements Serializable {

    private MockStatistics statistics;

    private List<MockRecords> records;

}
