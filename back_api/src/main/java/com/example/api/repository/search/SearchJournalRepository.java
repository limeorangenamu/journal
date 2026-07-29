package com.example.api.repository.search;

import com.example.api.entity.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchJournalRepository {
    public Page<Object[]> searchPage(String type, String keyword, Pageable pageable);
}
