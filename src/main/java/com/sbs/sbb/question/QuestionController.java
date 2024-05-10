package com.sbs.sbb.question;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/question")
@Controller
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Question> questionList = this.questionService.getList();
        model.addAttribute("questionList", questionList);

        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Question q = this.questionService.getQuestion(id);

        model.addAttribute("question", q);

        return "question_detail";
    }

    @GetMapping("/create")
    public String questionCreate() {
        return "question_form";
    }

    @PostMapping("/create")
    // QuestionForm 값을 바인딩 할때 유효성 체크를해라
    // QuestionFrom 변수는 model.addAttribute 없이 바로 뷰에서 접근할수있음.
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult) {
        if ( bindingResult.hasErrors()){
            // 다시작성하라는의미로 question_form으로 던짐
            return "question_form";
        }

        Question q = this.questionService.create(questionForm.getSubject(), questionForm.getContent());
        return "redirect:/question/list";
    }




}