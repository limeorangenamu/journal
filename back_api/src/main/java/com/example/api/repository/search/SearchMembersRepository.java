package com.example.api.repository.search;

import com.example.api.entity.Members;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchMembersRepository {
    Page<Members> searchPage(String type, String keyword, Pageable pageable);
}
