package com.sbs.sbb.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=200, message = "제목을 200자 이하로 입력해주세요.")
    private String subject;

    @NotEmpty(message="내용은 필수항목입니다.")
    @Size(max=20000, message = "제목을 20,000자 이하로 입력해주세요.")
    private String content;
}