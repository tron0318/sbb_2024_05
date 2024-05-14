package com.sbs.sbb.question;

import com.sbs.sbb.answer.AnswerForm;
import com.sbs.sbb.user.SiteUser;
import com.sbs.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/question")
@Controller
@RequiredArgsConstructor
//@Validated 컨트롤러에서는 이 부분 생략가능
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Question> paging = this.questionService.getList(page);
        model.addAttribute("paging", paging);

        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerFrom) {
        Question q = this.questionService.getQuestion(id);

        model.addAttribute("question", q);

        return "question_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    // QuestionFrom 변수는 model.addAttribute 없이 바로 뷰에서 접근할 수 있다.
    // QuestionFrom questionForm 써주는 이유 : question_form.html에서  questionForm 변수가 없으면 실행이 안되기 때문에
    // 빈 객체라도 만든다.
    // public String create(Model modle) {
    public String create(QuestionForm questionFrom) {
//        model.addAttribute("questionFrom", new QuestionForm());

        return "question_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    // QuestionForm 값을 바인딩 할 때 유효성 체크를 해라!
    // QuestionFrom 변수는 model.addAttribute 없이 바로 뷰에서 접근할 수 있다.
    public String questionCreate(
            @Valid QuestionForm questionForm,
            BindingResult bindingResult,
            Principal principal) {

        if ( bindingResult.hasErrors() ) {
            // question_form.html 실행
            // 다시 작성하라는 의미로 응답에 폼을 싫어서 보냄
            return "question_form";
        }

        SiteUser siteUser = this.userService.getUser(principal.getName());
        Question q = this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);

        return "redirect:/question/list";
    }
}