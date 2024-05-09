package com.sbs.sbb.answer;

import com.sbs.sbb.question.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service

public class AnswerService {
    private final AnswerRepository answerRepository;
    public Answer create(Question q, String content) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(q);
        answer.setCreateDate(LocalDateTime.now());
        this.answerRepository.save(answer);

        return answer;
    }
}
