package com.hamifs.config;

import com.hamifs.entity.User;
import com.hamifs.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 启动初始化：
 *  1) 执行 src/main/resources/db/init.sql（建表）
 *  2) 若 admin 不存在则插入 BCrypt(123456)
 *  注意：本类只在 dev / 演示用；生产请用 Flyway / Liquibase。
 */
@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        runInitSql();
        seedAdmin();
    }

    private void runInitSql() throws Exception {
        // init.sql 在 classpath:/db/init.sql
        Path p = Paths.get(AdminInitializer.class.getClassLoader()
                .getResource("db/init.sql").toURI());
        String sql = Files.readString(p, StandardCharsets.UTF_8);
        for (String stmt : sql.split(";")) {
            String s = stmt.trim();
            if (!s.isEmpty()) {
                try {
                    jdbcTemplate.execute(s);
                } catch (Exception e) {
                    log.warn("执行 init.sql 分句失败，已忽略：{}", e.getMessage());
                }
            }
        }
        log.info("init.sql 已执行完毕（建表）");
    }

    private void seedAdmin() {
        User exist = userMapper.selectByUsername("admin");
        if (exist != null) {
            log.info("admin 用户已存在（id={}），跳过初始化", exist.getId());
            return;
        }
        User u = new User();
        u.setUsername("admin");
        u.setPassword(passwordEncoder.encode("123456"));
        u.setName("管理员");
        u.setAvatar("");
        u.setStatus(1);
        int n = userMapper.insertIfAbsent(u);
        log.info("admin 用户初始化完成，受影响行数={}（0=已存在，1=新建）", n);
    }
}