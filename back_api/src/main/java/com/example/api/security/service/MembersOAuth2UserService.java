package com.example.api.security.service;

import com.example.api.repository.MembersRepository;
import com.example.api.entity.Members;
import com.example.api.entity.MembersRole;
import com.example.api.security.dto.MembersAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class MembersOAuth2UserService extends DefaultOAuth2UserService {
  private final MembersRepository membersRepository;
  private final PasswordEncoder passwordEncoder;

  enum SocialType {KAKAO, NAVER, GOOGLE}

  private SocialType getSocialType(String registrationId) {
    return switch (registrationId) {
      case "NAVER" -> SocialType.NAVER;
      case "KAKAO" -> SocialType.KAKAO;
      default -> SocialType.GOOGLE;
    };
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    SocialType socialType = getSocialType(registrationId.trim().toString());
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
    log.info("userNameAttributeName >> " + userNameAttributeName);

    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);
    oAuth2User.getAttributes().forEach((k, v) -> log.info(">>> " + k + ": " + v));

    String email = null;
    Members  members = null;
    if(socialType.name().equals("Google")) email = oAuth2User.getAttribute("email");
    else email = oAuth2User.getAttribute("email");

    Optional<Members> result = membersRepository.findByEmail(email);
    if(result.isPresent()) { // 4. 이미 등록 되어있을 때
      members = result.get();
    } else { // 등록 되지 않을 경우 DB로 저장해야 됨
      members = Members.builder()
          .email(email)
          .name(oAuth2User.getAttribute("name"))
          .password(passwordEncoder.encode("1"))
          .fromSocial(true)
          .build();
      members.addMemberRole(MembersRole.USER);
      membersRepository.save(members); // DB저장
    }
    System.out.println("members: " + members);
    // 세션 저장
    MembersAuthDTO dto = new MembersAuthDTO(
        members.getEmail(), members.getPassword(), true,
        members.getRoleSet().stream().map(
            role -> new SimpleGrantedAuthority("ROLE_" + role.name())
        ).collect(Collectors.toSet()),
        oAuth2User.getAttributes(),
        members.getEmail(), members.getName(), members.getMid()
    );

    return dto;
  }
}
