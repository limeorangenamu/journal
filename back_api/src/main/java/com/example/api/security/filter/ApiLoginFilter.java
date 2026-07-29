package com.example.api.security.filter;

import com.example.api.security.dto.MembersAuthDTO;
import com.example.api.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {
  private JWTUtil jwtUtil;

  public ApiLoginFilter(String defaultFilterProcessesUrl, JWTUtil jwtUtil) {
    super(defaultFilterProcessesUrl);
    this.jwtUtil = jwtUtil;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

    log.info("ApiLoginFilter..............attemptAuthentication");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    if(email == null) throw new BadCredentialsException("Email cannot be null");

    // ClubUserDetailsService의 loadUserByUsername()를 호출하고 인증
    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(email, password);
    return getAuthenticationManager().authenticate(authToken);

  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
    MembersAuthDTO membersAuthDTO = (MembersAuthDTO) authResult.getPrincipal();
    log.info("successful Authentication membersAuthDTO::" + membersAuthDTO);
    String email = membersAuthDTO.getEmail();
    String token = null;
    response.setContentType("application/json;charset=UTF-8");
    try {
      token = jwtUtil.generateToken(email);
      Map<String, Object> claims = new HashMap<>();
      claims.put("token", token);
      claims.put("email", email);
      claims.put("mid", membersAuthDTO.getMid());
      claims.put("name", membersAuthDTO.getName());

      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writeValueAsString(claims);
      response.getWriter().write(json);
      log.info("generated token: " + token);
    } catch (Exception e) {
      log.error("failed to generate token: " + e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Map<String, String> claims = new HashMap<>();
      claims.put("error", e.getMessage());
      ObjectMapper mapper = new ObjectMapper();
      response.getWriter().write(mapper.writeValueAsString(claims));
    }
  }
}
