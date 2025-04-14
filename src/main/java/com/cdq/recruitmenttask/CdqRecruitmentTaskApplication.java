package com.cdq.recruitmenttask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CdqRecruitmentTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(CdqRecruitmentTaskApplication.class, args);
    }

}
