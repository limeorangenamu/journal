package com.example.api.repository.search;

import com.example.api.entity.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchJournalRepository {
    default Page<Object[]> searchPage(String type, String keyword, Pageable pageable) {
        return searchPage(type, keyword, pageable, null, null);
    }

    Page<Object[]> searchPage(String type, String keyword, Pageable pageable, Long mid, Boolean isPublic);
}
