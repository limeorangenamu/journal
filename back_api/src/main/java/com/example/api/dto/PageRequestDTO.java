package com.example.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {
  private int page;
  private int size;
  private String type;
  private String keyword;

  // list 페이지를 처음 호출할 때 page, size 없음으로 기본생성자 실행
  public PageRequestDTO() {
    page = 1;size = 10;
  }

  // Pageable::repository에서 해당되는 Page<>목록을 들고오기 위한 보조 정보
  public Pageable getPageable(Sort sort){
    return PageRequest.of(page - 1, size, sort);
  }
}
