package com.example.api.security.filter;

import com.example.api.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {
  private String[] pattern;
  private AntPathMatcher antPathMatcher;
  private JWTUtil jwtUtil;

  public ApiCheckFilter(String[] pattern, JWTUtil jwtUtil) {
    this.pattern = pattern;
    this.jwtUtil = jwtUtil;
    antPathMatcher = new AntPathMatcher();
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    log.info("ApiCheckFilter.......................");
    log.info("Request URI: " + request.getRequestURI());
    log.info("Pattern >>> " + Arrays.toString(pattern));

    boolean check = false;
    for (int i = 0; i < pattern.length; i++) {
      if (antPathMatcher.match(request.getContextPath() + pattern[i], request.getRequestURI())) {
        log.info(">>>"+request.getContextPath() + pattern[i]);
        log.info(">>>"+request.getRequestURI());
        log.info(">>>"+antPathMatcher.match(request.getContextPath() + pattern[i], request.getRequestURI()));
        check = true; break;
      }
    }
    if(check) { // 주소가 맞을 경우
      if (checkAuthHeader(request)) { // 토큰을 가질 경우
        log.info(">>>checkAuthHeader true");
        filterChain.doFilter(request, response); // 토큰이 있기 때문에 그 다음 주소로 넘어감.
        return;
      } else { // 토큰이 없는 경우
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        String message = "FAIL CHECK API TOKEN";
        json.put("code", "403");
        json.put("message", message);
        PrintWriter out = response.getWriter();
        out.println(json);
        return;
      }
    } else {
      // 경로가 보호 대상이 아니면 필터 통과
      log.info("Request does not match protected patterns. Skipping JWT check.");
    }

    filterChain.doFilter(request, response);
  }

  private boolean checkAuthHeader(HttpServletRequest request) {
    boolean checkResult = false;
    String authHeader = request.getHeader("Authorization");

    // JWT로 시작하는 경우 Basic 아닌, Bearer 시작
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      log.info("Authorization existed: " + authHeader);
      // if (authHeader.equals("12345678")) checkResult = true;
      try {
        String email = jwtUtil.validateAndExtract(authHeader.substring(7));
        log.info("Validate result: " + email);
        checkResult = email.length() > 0;
        log.info("checkResult>>"+checkResult);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return checkResult;
  }
}
