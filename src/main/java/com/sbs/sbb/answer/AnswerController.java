package com.sbs.sbb.answer;

import com.sbs.sbb.question.Question;
import com.sbs.sbb.question.QuestionService;
import com.sbs.sbb.user.SiteUser;
import com.sbs.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequestMapping("/answer")
@Controller
@RequiredArgsConstructor
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;


    @PostMapping("/create/{id}")
    public String createAnswer(
            Model model,
            @PathVariable("id")
            Integer id,
            @Valid AnswerForm answerForm,
            BindingResult bindingResult,
            Principal principal
    ) {

        // 답변 부모 질문객체를 받아온다.
        Question q = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if ( bindingResult.hasErrors() ) {
            model.addAttribute("question", q);
            return "question_detail";
        }

        Answer answer = this.answerService.create(q, answerForm.getContent(), siteUser);

        return "redirect:/question/detail/%d".formatted(id);
    }
}