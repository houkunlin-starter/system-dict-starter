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

import java.util.Objects;

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
        final String expect = "{\"id\":1," +
            "\"gender\":\"0\",\"genderText\":\"男\"," +
            "\"sex\":\"1\",\"sexName\":\"女\"," +
            "\"type\":\"0\",\"typeText\":\"系统管理员\"," +
            "\"typeArrays0\":\"0,1\",\"typeArrays0Text\":\"系统管理员、普通用户\"," +
            "\"typeArrays1\":\"0,1\",\"typeArrays1Text\":[\"系统管理员\",\"普通用户\"]," +
            "\"typeArrays2\":[\"系统管理员\",\"普通用户\"]," +
            "\"typeEnum0\":\"0\",\"typeEnum0Text\":\"系统管理员\"," +
            "\"typeEnum1\":\"系统管理员\"," +
            "\"name\":\"用户姓名\"}";
        final User user = User.builder().id(1)
            .gender(0)
            .sex(1)
            .type(0)
            .typeArrays0("0,1")
            .typeArrays1("0,1")
            .typeArrays2("0,1")
            .typeEnum0("0")
            .typeEnum1("0")
            .name("用户姓名").build();
        final String userJson = objectMapper.writeValueAsString(user);
        logger.info("JSON输出 {}", userJson);
        logger.info("预期比对 {}", Objects.equals(userJson, expect));
    }
}
