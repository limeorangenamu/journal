package com.example.api.controller;

import com.example.api.dto.JournalDTO;
import com.example.api.dto.PageRequestDTO;
import com.example.api.dto.RemoveJournalResponseDTO;
import com.example.api.entity.Journal;
import com.example.api.repository.JournalRepository;
import com.example.api.service.JournalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/journal")
public class JournalController {
    private final JournalService journalService;

    @Value("${com.example.upload.path}")
    private String uploadPath;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> list(PageRequestDTO pageRequestDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("pageResultDTO", journalService.getList(pageRequestDTO));
        map.put("pageRequestDTO", pageRequestDTO);
        return ResponseEntity.ok(map);
    }

    @GetMapping(value = "/my-list/{mid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> myList(@PathVariable Long mid, PageRequestDTO pageRequestDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("pageResultDTO", journalService.getMyList(pageRequestDTO, mid));
        map.put("pageRequestDTO", pageRequestDTO);
        return ResponseEntity.ok(map);
    }

    @GetMapping(value = "/community/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> publicList(
            PageRequestDTO pageRequestDTO,
            @RequestParam(defaultValue = "latest") String sort) {
        Map<String, Object> map = new HashMap<>();
        map.put("pageResultDTO", journalService.getPublicList(pageRequestDTO, "popular".equals(sort)));
        map.put("pageRequestDTO", pageRequestDTO);
        return ResponseEntity.ok(map);
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Long> registerJournal(@RequestBody JournalDTO journalDTO) {
        return ResponseEntity.ok((Long) journalService.register(journalDTO));
    }

    @GetMapping(value = {"read/{jno}", "modify/{jno}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, JournalDTO>> get(@PathVariable("jno") Long jno) {
        JournalDTO journalDTO = journalService.get(jno);
        Map<String, JournalDTO> map = new HashMap<>();
        map.put("journalDTO", journalDTO);
        return ResponseEntity.ok(map);
    }

    @PutMapping(value = "/modify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> modify(@RequestBody JournalDTO journalDTO) {
        journalService.modify(journalDTO);
        Map<String, String> result = new HashMap<>();
        result.put("msg", journalDTO.getJno() + " 수정");
        result.put("jno", journalDTO.getJno() + "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/remove/{jno}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RemoveJournalResponseDTO> remove(
            @PathVariable("jno") Long jno, @RequestBody PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(journalService.removeJournalWithCommentsAndPhotos(jno, pageRequestDTO));
    }
}

