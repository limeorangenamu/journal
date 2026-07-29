package com.example.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"journal", "members"})
public class Comments extends BasicEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long cno;

  @ManyToOne(fetch = FetchType.LAZY)
  private Journal journal;
  @ManyToOne(fetch = FetchType.LAZY)
  private Members members;

  private Long likes; // 좋아요
  private String text; // 한줄평

  public void changeLikes(Long likes) {this.likes = likes;}
  public void changeText(String text) {this.text = text;}
}
