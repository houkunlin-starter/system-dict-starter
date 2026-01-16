package com.houkunlin.dict;

import com.houkunlin.dict.common.bean.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 把元使用示例的代码移到单元测试中
 *
 * @author HouKunLin
 */
@RestController
@RequestMapping("/test/")
public class ExamplesController {

    @GetMapping("/user-get")
    public ResponseEntity<User> get(final User user) {
        return ResponseEntity.ok()
            // MockMvc 遇到调用结果编码为 ISO-8859-1 的问题
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(user);
    }

    // @PostMapping("/user-post")
    // public ResponseEntity<User> post(@RequestBody final User user) {
    //     return ResponseEntity.ok()
    //         // MockMvc 遇到调用结果编码为 ISO-8859-1 的问题
    //         .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
    //         .body(user);
    // }
}
