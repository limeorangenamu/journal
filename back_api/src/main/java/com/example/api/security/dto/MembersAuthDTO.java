package com.example.api.security.dto;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Log4j2
@Getter
@Setter
@ToString
public class MembersAuthDTO extends User implements OAuth2User {
  private Long mid;
  private String email;
  private String password;
  private String name;
  private boolean fromSocial;

  private Map<String, Object> attr;

  public MembersAuthDTO(
      String username, @Nullable String password,
      boolean fromSocial,
      Collection<? extends GrantedAuthority> authorities,
      String email, String name, Long mid
  ) {
    super(username, password, authorities);
    this.email = email;this.password = password;this.name=name; this.fromSocial=fromSocial;this.mid=mid;
  }

  public MembersAuthDTO(String username, @Nullable String password, boolean fromSocial,
                        Collection<? extends GrantedAuthority> authorities,
                        Map<String, Object> attr, String email, String name, Long mid) {
    this(username, password, fromSocial, authorities, email, name, mid);
    this.attr = attr;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attr;
  }
}
