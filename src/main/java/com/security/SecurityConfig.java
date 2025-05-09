package com.security;

import com.member.model.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 BCryptPasswordEncoder 進行密碼加密
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) //關閉 CSRF 保護
                .httpBasic(basic -> basic.disable()) // 關閉 HTTP 基本認證
                .formLogin(form -> form.disable()) // 關閉security表單登入

                .authorizeHttpRequests(request -> request
                        .anyRequest().permitAll()
                )
                .oauth2Login( oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)//指定自訂service
                        )
                        .successHandler(customAuthenticationSuccessHandler) //指定登入成功後網頁導向
                        .failureUrl("/login")// 登入失敗導向登入畫面
                )
                .build();
    }
}
