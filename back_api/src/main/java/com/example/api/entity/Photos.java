package com.example.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "journal")
public class Photos extends BasicEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long pno;
  private String uuid;
  private String photosName;
  private String path;
  @ManyToOne(fetch = FetchType.LAZY)
  private Journal journal;
}
