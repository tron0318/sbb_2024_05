package com.sbs.sbb.answer;

import com.sbs.sbb.question.Question;
import com.sbs.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity // answer 테이블
public class Answer {
    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Integer id;

    @Column(columnDefinition = "TEXT") // text
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    // Many = Answer, One = Question
    // 필수
    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voters = new LinkedHashSet<>();

    public void addVoter(SiteUser voter) {
        voters.add(voter);
    }
}