package com.example.api.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
// list.html 페이지에서 사용할 페이지의 정보들을 담은 객체
public class PageResultDTO<DTO, EN> {
  private List<DTO> dtoList; // 해당 페이지의 목록
  private int totalPage; // 10개씩 했을 때 총 페이지수
  private int page; // 요청한 페이지 번호
  private int size; // 한페이지당 나올 갯수
  private int start, end; // 페이지네이션의 시작번호, 끝번호
  private boolean prev, next; // 다음, 이전 버튼에 대한 정보
  private List<Integer> pageList; // 페이지 번호 목록 :: page list

  // Page는 repository에서 가져오고, Function은 Entity를 DTO로 변환함.
  public PageResultDTO(Page<EN> page, Function<EN, DTO> fn) {

    // page의 목록에 대한 리스트를 List타입으로 변경함.
    dtoList = page.stream().map(fn).collect(Collectors.toList());

    // Page객체로 부터 전체 페이지 개수 정보 구하기
    totalPage = page.getTotalPages(); // 실제 총 페이지수

    // Page의 pageable 객체를 통해서 나머지 정보를 완성
    makePageInfo(page.getPageable());
  }

  private void makePageInfo(Pageable pageable) {
    page = pageable.getPageNumber() + 1;
    size = pageable.getPageSize();
    //페이지네이션의 시작 번호
    int tempEnd = (int)(Math.ceil(page/10.0))*10;
    start = tempEnd - 9;
    //계산상 끝페이지가 실제총페이보다 작으면 계산상 끝페이지를 페이지네이션 끝페이지가 됨.
    prev = start > 1;
    end = tempEnd < totalPage ? tempEnd : totalPage;
    next = tempEnd < totalPage;
    //페이지 번호 목록
    pageList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
  }

}
