package com.example.api.controller;

import com.example.api.dto.MembersDTO;
import com.example.api.service.MembersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/members")
public class MembersController {
  private final MembersService membersService;

  @PostMapping(value = "/register")
  public ResponseEntity<Long> register(@RequestBody MembersDTO membersDTO){
    Long mid = membersService.registerMembers(membersDTO);
//    return new ResponseEntity<>(mid, HttpStatus.OK);
    return ResponseEntity.ok(mid);
  }

  @GetMapping(value = "/get/{mid}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MembersDTO> read(@PathVariable("mid") Long mid){
    log.info("read members by mid {}", mid);
    return new ResponseEntity<>(membersService.getMembers(mid), HttpStatus.OK);
  }

  @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MembersDTO> get(String email){
    log.info("get members by email {}", email);
    return new ResponseEntity<>(membersService.getMembersByEmail(email), HttpStatus.OK);
  }
}
