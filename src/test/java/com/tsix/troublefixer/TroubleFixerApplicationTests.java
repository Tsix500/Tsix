package com.tsix.troublefixer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.File;

import static com.troublefixer.utils.FileUtils.checkType;

@SpringBootTest
class TroubleFixerApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("name","lihua");
        System.out.println(redisTemplate.opsForValue().get("name"));
    }

    @Test
    void testfile() {
        File f = new File("pwd.csv");
//        System.out.println(checkType(f,"csv"));
    }
}
