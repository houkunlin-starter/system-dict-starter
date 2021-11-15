package com.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.SystemDictScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 最简单的使用示例
 *
 * @author HouKunLin
 */
@SpringBootApplication
/// @SystemDictScan // 是必须加的一个注解
@SystemDictScan
@RestController
@RequestMapping
public class Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private final ObjectMapper objectMapper;

    public Application(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping
    public User index(final User user) {
        return user;
    }

    @Override
    public void run(final String... args) throws Exception {
        final String expect = "\"hobby\":\"1\",\"hobbyText\":\"打篮球\",\"nation\":\"2\",\"nationText\":\"回族\",\"createdBy\":\"12\",\"createdByText\":\"用户姓名 - 12\"";
        final User1 user = User1.builderUser1()
            .hobby("1")
            .createdBy("12")
            .nation("2")
            .build();
        final String userJson = objectMapper.writeValueAsString(user);
        logger.info("JSON输出 {}", userJson);
        logger.info("预期比对 {}", userJson.contains(expect));
    }
}
