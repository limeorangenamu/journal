package com.example.api.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

@Log4j2
public class JWTUtil {
  private String secretKey = "1234567890abcdefghijklmnopqrstuvwxyz";
  private long expire = 60 * 24 * 30;

  public String generateToken(String content) throws Exception {
    return Jwts.builder()
        .issuedAt(new Date())
        .expiration(Date.from(ZonedDateTime.now().plusMinutes(expire).toInstant()))
        //.expiration(Date.from(ZonedDateTime.now().plusSeconds(1).toInstant()))
        .claim("sub", content)
        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
        .compact();
  }

  public String validateAndExtract(String tokenStr) throws Exception {
    Jwt jwt = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
        .build().parse(tokenStr);
    log.info("Jwts getClass; " + jwt);
    Claims claims = (Claims) jwt.getPayload();
    return (String) claims.get("sub");
  }
}
