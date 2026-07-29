package com.example.api.repository;

import com.example.api.entity.Members;
import com.example.api.entity.MembersRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MembersRepositoryTests {
  @Autowired
  private MembersRepository membersRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void insertDummies() {
    IntStream.rangeClosed(1, 100).forEach(i -> {
      Members members = Members.builder()
          .email("user" + i + "@a.a")
          .name(generateName())
          .nickname("User" + i)
          .fromSocial(false)
          .mobile("010-" + randomMobile() + "-" + randomMobile())
          .password(passwordEncoder.encode("1"))
          .build();
      members.addMemberRole(MembersRole.USER);
      if(i>80) members.addMemberRole(MembersRole.MANAGER);
      if(i>90) members.addMemberRole(MembersRole.ADMIN);
      membersRepository.save(members);
    });
  }

  private String randomMobile() {
    int i = (int) (Math.random() * 10000);
    return (i < 10) ? "000" + i : (i < 100) ? "00" + i : (i < 1000) ? "0" + i : "" + i;
  }

  private String generateName() {
    String[] arr1 = new String[]{"김", "이", "방", "강", "조", "황", "송", "유", "권", "신", "심"};
    String[] arr2 = new String[]{"일", "이", "삼", "사", "오"};
    String[] arr3 = new String[]{"진", "은", "영", "호", "원"};
    return arr1[(int) (Math.random() * arr1.length)]
        + arr2[(int) (Math.random() * arr2.length)]
        + arr3[(int) (Math.random() * arr3.length)];
  }
}