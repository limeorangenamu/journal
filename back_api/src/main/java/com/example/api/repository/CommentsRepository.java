package com.example.api.repository;

import com.example.api.entity.Comments;
import com.example.api.entity.Journal;
import com.example.api.entity.Members;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
    @EntityGraph(attributePaths = {"members"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Comments> findByJournal(Journal journal);

    @Modifying // update or delete 할 경우 반드시 필요
    @Query("delete from Comments c where c.members = :members ")
    void deleteByMembers(Members members);

    @Modifying // update or delete 할 경우 반드시 필요
    @Query("delete from Comments c where c.journal.jno = :jno ")
    void deleteByJno(@Param("jno") Long jno);
}
