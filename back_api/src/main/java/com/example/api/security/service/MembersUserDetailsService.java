package com.example.api.security.service;

import com.example.api.entity.Members;
import com.example.api.entity.MembersRole;
import com.example.api.repository.MembersRepository;
import com.example.api.security.dto.MembersAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class MembersUserDetailsService implements UserDetailsService {
  private final MembersRepository membersRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("loadUserByUsername.............");
    Optional<Members> result = membersRepository.findByEmail(username);
    if(!result.isPresent()) throw new UsernameNotFoundException("Check Email or Social");
    Members members = result.get();
    MembersAuthDTO dto = new MembersAuthDTO(
        members.getEmail(), members.getPassword(), members.getFromSocial(),
        members.getRoleSet().stream().map(new Function<MembersRole, SimpleGrantedAuthority>() {
          @Override
          public SimpleGrantedAuthority apply(MembersRole membersRole) {
            return new SimpleGrantedAuthority("ROLE_"+membersRole.name());
          }
        }).collect(Collectors.toSet())
        , members.getEmail(), members.getName(), members.getMid()
    );
    return dto;
  }
}
