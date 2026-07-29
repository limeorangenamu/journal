package com.example.api.config;

import com.example.api.security.filter.ApiCheckFilter;
import com.example.api.security.filter.ApiLoginFilter;
import com.example.api.security.filter.CORSFilter;
import com.example.api.security.handler.ApiLoginFailHandler;
import com.example.api.security.util.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) //메서드 단위 보안 설정, AOP
public class SecurityConfig {
  private static final String[] AUTH_WHITELIST = {
      "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html"
      , "/members/register", "/display/**"

  };
  private static final String[] AUTH_CHECKLIST = {
      "/members/get/**", "/uploadAjax"  // API 체크하고 토큰 여부 확인할 주소
      , "/journal/**", "/removeFile/**", "/comments/**"
  };

  @Bean
  protected SecurityFilterChain config(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.csrf(csrf -> csrf.disable());// csrf 사용안할 경우

    //httpSecurity.cors(Customizer.withDefaults()); //기본값 사용
    /*httpSecurity.cors(cors -> cors.disable()); // cors 필터를 사용함으로 기본값 disable
    httpSecurity.addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class);*/

    httpSecurity.authorizeHttpRequests(auth -> {
      auth
          .requestMatchers(AUTH_WHITELIST).permitAll()
          .requestMatchers(AUTH_CHECKLIST).permitAll()
          .anyRequest().denyAll();
    });

    httpSecurity.addFilterBefore(apiCheckFilter(), UsernamePasswordAuthenticationFilter.class);
    httpSecurity.addFilterBefore(
        apiLoginFilter(httpSecurity.getSharedObject(AuthenticationConfiguration.class))
        , UsernamePasswordAuthenticationFilter.class
    );

    return httpSecurity.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ApiCheckFilter apiCheckFilter() {
    return new ApiCheckFilter(AUTH_CHECKLIST, jwtUtil());
  }

  @Bean
  public ApiLoginFilter apiLoginFilter(AuthenticationConfiguration ac) throws Exception {
    ApiLoginFilter apiLoginFilter = new ApiLoginFilter("/login", jwtUtil());
    apiLoginFilter.setAuthenticationManager(ac.getAuthenticationManager());
    apiLoginFilter.setAuthenticationFailureHandler(apiLoginFailHandler());
    return apiLoginFilter;
  }

  @Bean
  public ApiLoginFailHandler apiLoginFailHandler() {
    return new ApiLoginFailHandler();
  }

  @Bean
  public JWTUtil jwtUtil() {
    return new JWTUtil();
  }

  @Bean
  public CORSFilter corsFilter() {
    return new CORSFilter();
  }
}
