package com.example.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JournalDTO {
  private Long jno;
  private String title;
  private String content;

  @Builder.Default // Journal과 Photos가 LAZY로 설정, AllArgsConstructor하면 기본 값으로 초기화
  private List<PhotosDTO> photosDTOList = new ArrayList<>();

  private MembersDTO membersDTO;
  private Long likes;
  private Long commentsCnt;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
