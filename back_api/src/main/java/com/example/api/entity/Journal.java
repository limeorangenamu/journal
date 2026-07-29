package com.example.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"members"})
public class Journal extends BasicEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long jno;
  private String title;
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  private Members members;

  public void changeTitle(String title) {this.title = title;}
  public void changeContent(String content) {this.content = content;}
}
