package com.example.api.repository;

import com.example.api.entity.Journal;
import com.example.api.repository.search.SearchJournalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long>, SearchJournalRepository {
    // Journal, 좋아요 합, Comment 개수
//    @Query("select j, sum(coalesce(c.likes, 0)), count(distinct c) " +
//            "from Journal j left outer join Comments c on c.journal=j group by j ")
//    Page<Object[]> getListPage(Pageable pageable);

    // Journal, Photos, 평점평균, comment 갯수 :: 4개 => 문제점::이미지 만큼 무비도 복수출력
//    @Query("select j, p, sum(coalesce(c.likes, 0)), count(c) " +
//            "from Journal j left outer join Photos p on p.journal = j " +
//            "left outer join Comments c on c.journal = j group by j, p ")
//    Page<Object[]> getListPage2(Pageable pageable);

    // 모든 저널 페이징 처리하기 :: MySQL, getListAllJournalPhotosMax
    @Query("""
            select j, p, sum(coalesce(c.likes, 0)), count(distinct c)
            from Journal j left join Photos p on p.journal = j left join Comments c on c.journal = j
            where p.pno = (select max(p2.pno) from Photos p2 where p2.journal = j)
            group by j, p
            order by j.jno desc
            """)
    Page<Object[]> getListPageMaxPhotos(Pageable pageable);

    // 상세보기 페이지 전용 메서드
    @Query("""
    select j, p, m, sum(coalesce(c.likes, 0)), count(distinct c)
    from Journal j
    join j.members m
    left join Photos p on p.journal = j
    left join Comments c on c.journal = j
    where j.jno = :jno
    group by j, p, m
    order by p.pno asc
    """)
    List<Object[]> getJournalDetail(@Param("jno") Long jno);

    // 개별 회원의 저널 모두 보기
    @Query("""
            select j, p, sum(coalesce(c.likes, 0)), count(distinct c)
            from Journal j left join Photos p on p.journal = j left join Comments c on c.journal = j
            where p.pno = (select max(p2.pno) from Photos p2 where p2.journal = j)
            and j.members.mid = :mid
            group by j
            order by j.jno desc
            """)
    List<Object[]> getListMyJournal(Pageable pageable, @Param("mid") Long mid);
}
