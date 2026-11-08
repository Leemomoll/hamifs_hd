package com.hamifs.config;

import com.hamifs.common.ResultCode;
import com.hamifs.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置：
 * - /auth/login 放行（代理已剥掉 /api 前缀，这里注册的是剥完后的路径）
 * - /auth/** 其他接口必须带 token
 * - 401 / 403 翻译为 {code,message,data} 信封（直接拼 JSON 字符串）
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider(uds);
        p.setPasswordEncoder(encoder);
        return p;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authProvider) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .exceptionHandling(eh -> eh
                    .authenticationEntryPoint((req, resp, ex) -> writeJson(resp, HttpStatus.UNAUTHORIZED,
                            ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage()))
                    .accessDeniedHandler((req, resp, ex) -> writeJson(resp, HttpStatus.FORBIDDEN,
                            ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage()))
            )
            .authorizeHttpRequests(reg -> reg
                    .requestMatchers("/auth/login").permitAll()
                    .requestMatchers("/error", "/favicon.ico").permitAll()
                    .anyRequest().authenticated()
            )
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** 直接拼 JSON，避免引入 jackson-databind。 */
    private void writeJson(jakarta.servlet.http.HttpServletResponse resp,
                           HttpStatus status,
                           Integer code,
                           String message) throws java.io.IOException {
        resp.setStatus(status.value());
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setCharacterEncoding("UTF-8");
        String body = "{\"code\":" + code
                + ",\"message\":\"" + escape(message) + "\""
                + ",\"data\":null}";
        resp.getWriter().write(body);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}