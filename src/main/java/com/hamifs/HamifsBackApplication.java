package com.hamifs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hamifs.mapper") // 扫描mapper包
public class HamifsBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(HamifsBackApplication.class, args);
    }

}
