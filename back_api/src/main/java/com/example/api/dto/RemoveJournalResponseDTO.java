package com.example.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoveJournalResponseDTO {
    // Journal 삭제후 응답을 위한 DTO
    private Long jno;
    private int page;
    private String type;
    private String keyword;
    private String message;
}
