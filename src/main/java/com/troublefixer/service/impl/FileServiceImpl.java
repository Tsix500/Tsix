package com.troublefixer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.troublefixer.mapper.MockRecordsMapper;
import com.troublefixer.mapper.MockStatisticsMapper;
import com.troublefixer.pojo.MockRecords;
import com.troublefixer.pojo.MockStatistics;
import com.troublefixer.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MockRecordsMapper recordsMapper;

    private final MockStatisticsMapper statisticsMapper;

    private final String relativePath = "recordsfile/";

    private List<MockRecords> getAllRecords(Long mockId) {
        LambdaQueryWrapper<MockRecords> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MockRecords::getMockId, mockId);
        return recordsMapper.selectList(lqw);
    }

    @Override
    public String generateFile(Long mockId, String path) {
        MockStatistics statistics = statisticsMapper.selectById(mockId);
        List<MockRecords> records = getAllRecords(mockId);
        String fileName = UUID.randomUUID() + ".txt";
        File dir = new File(path);
        path += fileName;
        File file = new File(path);
        try {
            if(!dir.exists()){
                dir.mkdirs();
            }
            System.out.println(path);
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw);){
            bw.write(statistics.toString());
            bw.write("\n测试结果:{");
            for(MockRecords record : records){
                bw.write(record.toString());
            }
            bw.write("}");
            bw.flush();
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
