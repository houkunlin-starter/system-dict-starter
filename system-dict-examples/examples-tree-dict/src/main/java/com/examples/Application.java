package com.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.SystemDictScan;
import com.houkunlin.system.dict.starter.json.Array;
import com.houkunlin.system.dict.starter.json.DictText;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SystemDictScan 是必须加的一个注解
 *
 * @author HouKunLin
 */
@SpringBootApplication
@SystemDictScan
@RestController
@RequestMapping
@Component
@RequiredArgsConstructor
public class Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private final ObjectMapper objectMapper;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        final Bean bean = Bean.builder()
            .treeData1("2")
            .treeData2("2-2")
            .city1("110101")
            .city2("130102")
            .city3("130203,130204,130205")
            .build();
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean));
    }

    @Data
    @Builder
    @AllArgsConstructor
    static class Bean {
        @DictText(value = "TreeData", tree = true)
        private String treeData1;
        @DictText(value = "TreeData", tree = true)
        private String treeData2;
        @DictText(value = "City", tree = true)
        private String city1;
        @DictText(value = "City", tree = true)
        private String city2;
        @DictText(value = "City", tree = true, array = @Array(toText = false))
        private String city3;
    }
}
