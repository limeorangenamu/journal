package com.example.api.repository;

import com.example.api.entity.Members;
import com.example.api.repository.search.SearchMembersRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members, Long>, SearchMembersRepository {

  @EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("select m from Members m where m.email=:email ")
  Optional<Members> findByEmail(@Param("email") String email);
}
