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
  @Builder.Default
  private boolean isPublic = true;
  @Builder.Default
  private Long views = 0L;

  @ManyToOne(fetch = FetchType.LAZY)
  private Members members;

  public void changeTitle(String title) {this.title = title;}
  public void changeContent(String content) {this.content = content;}
  public void changePublic(boolean isPublic) {this.isPublic = isPublic;}
  public void increaseViews() {this.views = (this.views == null ? 0L : this.views) + 1;}
}
