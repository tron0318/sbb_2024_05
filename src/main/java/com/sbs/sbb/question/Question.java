package com.sbs.sbb.question;

import com.sbs.sbb.answer.Answer;
import com.sbs.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity // question 테이블
@ToString
public class Question {
    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Integer id;

    @Column(length = 200) // VARCHAR(200)
    private String subject;

    @Column(columnDefinition = "TEXT") // TEXT
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    // mappedBy Answer 클래스의 question 변수 이름을 적어야 함.
    // CascadeType.REMOVE를 하면 Question을 삭제할 때 답변도 함께 삭제됨.
    // OneToMany는 테이블의 컬럼으로 생성되지 않음.
    // 선택
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.EXTRA)
    //answerList.size(); 함수가 실행 될 때 SELECT COUNT 실행
    private List<Answer> answerList = new ArrayList<>();

    @ManyToOne
    private SiteUser author;
}