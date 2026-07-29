package com.example.api.repository;

import com.example.api.entity.Journal;
import com.example.api.entity.Members;
import com.example.api.entity.Photos;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.LongStream;

@SpringBootTest
class JournalRepositoryTests {
  @Autowired
  private JournalRepository journalRepository;
  @Autowired
  private PhotosRepository photosRepository;

  @Test
  @Transactional
  @Commit
  public void insertJournals() {
    LongStream.rangeClosed(1, 100).forEach(i -> {
      Journal journal = Journal.builder()
          .title("Title..." + i)
          .content("Content..." + i)
          .members(Members.builder().mid(i).build())
          .build();
      journalRepository.save(journal);

      int count = (int) (Math.random() * 5) + 1;
      for (int j = 0; j < count; j++) {
        Photos photos = Photos.builder()
            .uuid(UUID.randomUUID().toString())
            .journal(journal)
            .photosName("photos" + j + ".jpg")
            .build();
        photosRepository.save(photos);
      }
    });
  }

  @Test
  public void testGetListPage() {
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "jno"));
    Page<Object[]> result = journalRepository.getListPageMaxPhotos(pageRequest);
    for (Object[] objects : result.getContent()) {
      System.out.println(Arrays.toString(objects));
    }
  }

  @Transactional
  @Test
  public void testSearchPage() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("jno").descending().and(Sort.by("title").ascending()));
    Page<Object[]> result = journalRepository.searchPage("t", "1", pageable);
    for (Object[] objects : result.getContent()) {
      System.out.println(Arrays.toString(objects));
    }
  }

  @Test
  public void testGetListPagePhotos() {
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "jno"));
    Page<Object[]> result = journalRepository.getListPageMaxPhotos(pageRequest);
    for (Object[] objects : result.getContent()) {
      System.out.println(Arrays.toString(objects));
    }
  }

  @Test
  public void testGetListPagePhotosJPQL() {
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "jno"));
    Page<Object[]> result = journalRepository.getListPageMaxPhotos(pageRequest);
    for (Object[] objects : result.getContent()) {
      System.out.println(Arrays.toString(objects));
    }
  }

//  @Test
//  public void testGetMaxQuery() {
//    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "pno"));
//    Page<Object[]> result = journalRepository.getMaxQuery(pageRequest);
//    for (Object[] objects : result.getContent()) {
//      System.out.println(Arrays.toString(objects));
//    }
//  }

//  @Test
//  public void testGetJournalWithAll() {
//    List<Object[]> result = journalRepository.getJournalWithAll(1L);
//    for (Object[] objects : result) {
//      System.out.println(Arrays.toString(objects));
//    }
//  }


}