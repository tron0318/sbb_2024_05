package com.sbs.sbb;

import com.sbs.sbb.answer.Answer;
import com.sbs.sbb.answer.AnswerRepository;
import com.sbs.sbb.answer.AnswerService;
import com.sbs.sbb.question.Question;
import com.sbs.sbb.question.QuestionRepository;
import com.sbs.sbb.question.QuestionService;
import com.sbs.sbb.user.SiteUser;
import com.sbs.sbb.user.UserRepository;
import com.sbs.sbb.user.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SbbApplicationTests {
	@Autowired
	private QuestionService questionService;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private UserService userService;
	@Autowired
	private QuestionRepository questionRepository;
	@Autowired
	private AnswerRepository answerRepository;
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
		// 아래 메서드는 각 테스트케이스가 실행되기 전에 실행된다.
	void beforeEach() {
		// 모든 데이터 삭제
		answerRepository.deleteAll();
		answerRepository.clearAutoIncrement();

		// 모든 데이터 삭제
		questionRepository.deleteAll();
		questionRepository.clearAutoIncrement();

		// 모든 데이터 삭제
		// 흔적 삭제 -> 다음번 INSERT를 할 때 id가 1번으로 설정되도록
		userRepository.deleteAll();
		userRepository.clearAutoIncrement();

		// 회원 2명 생성
		SiteUser user1 = userService.create("user1", "user1@test.com", "1234");
		SiteUser user2 = userService.create("user2", "user2@test.com", "1234");

		// 질문 1개 생성
		Question q1 = questionService.create("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.", user1);

		// 질문 1개 생성
		Question q2 = questionService.create("스프링부트 모델 질문입니다.", "id는 자동으로 생성되나요?", user2);

		// 답변 1개 생성
		Answer a1 = answerService.create(q2, "네 자동으로 생성됩니다.", user2);

		// 1번 질문에 2명의 회원이 추천을 한다.
		// user1 (이)가 q1 (을)를 추천했다.
		q1.addVoter(user1);
		q1.addVoter(user2);
		questionRepository.save(q1);

		// 2번 질문에 2명의 회원이 추천을 한다.
		q2.addVoter(user1);
		q2.addVoter(user2);
		questionRepository.save(q2);

		// 1번 답변에 2명의 회원이 추천을 한다.
		a1.addVoter(user1);
		a1.addVoter(user2);
		answerRepository.save(a1);
	}

	@Test
	@DisplayName("데이터 저장")
	void t001() {
		SiteUser user1 = userService.getUser("user1");

		// 질문 1개 생성
		Question q1 = questionService.create("세계에서 가장 부유한 국가가 어디인가요?", "알고 싶습니다.", user1);

		assertEquals("세계에서 가장 부유한 국가가 어디인가요?", questionRepository.findById(3).get().getSubject());
	}

	/*
    SQL
    SELECT * FROM question
    */
	@Test
	@DisplayName("findAll")
	void t002() {
		List<Question> all = questionRepository.findAll();
		assertEquals(2, all.size());

		Question q = all.get(0);
		assertEquals("sbb가 무엇인가요?", q.getSubject());
	}

	/*
    SQL
    SELECT *
    FROM question
    WHERE id = 1
    */
	@Test
	@DisplayName("findById")
	void t003() {
		Optional<Question> oq = questionRepository.findById(1);

		if (oq.isPresent()) {
			Question q = oq.get();
			assertEquals("sbb가 무엇인가요?", q.getSubject());
		}
	}

	/*
    SQL
    SELECT *
    FROM question
    WHERE subject = 'sbb가 무엇인가요?'
    */
	@Test
	@DisplayName("findBySubject")
	void t004() {
		Question q = questionRepository.findBySubject("sbb가 무엇인가요?");
		assertEquals(1, q.getId());
	}

	/*
    SQL
    SELECT *
    FROM question
    WHERE subject = 'sbb가 무엇인가요?'
    AND content = 'sbb에 대해서 알고 싶습니다.'
    */
	@Test
	@DisplayName("findBySubjectAndContent")
	void t005() {
		Question q = questionRepository.findBySubjectAndContent(
				"sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."
		);
		assertEquals(1, q.getId());
	}

	/*
    SQL
    UPDATE
        question
    SET
        content = ?,
        create_date = ?,
        subject = ?
    WHERE
        id = ?
    */
	@Test
	@DisplayName("데이터 수정하기")
	void t007() {
		Optional<Question> oq = questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Question q = oq.get();
		q.setSubject("수정된 제목");
		questionRepository.save(q);
	}

	/*
    SQL
    DELETE
    FROM
        question
    WHERE
        id = ?
    */
	@Test
	@DisplayName("데이터 삭제하기")
	void t008() {
		// questionRepository.count()
		// SQL : SELECT COUNT(*) FROM question;
		assertEquals(2, questionRepository.count());
		Optional<Question> oq = questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Question q = oq.get();
		questionRepository.delete(q);
		assertEquals(1, questionRepository.count());
	}

	@Test
	@DisplayName("답변 데이터 생성 후 저장하기")
	void t009() {
		Optional<Question> oq = this.questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

        /*
        // v1
        Optional<Question> oq = questionRepository.findById(2);
        Question q = oq.get();
        */

        /*
        // v2
        Question q = questionRepository.findById(2).get();
        */
		SiteUser user2 = userService.getUser("user2");

		Answer a = answerService.create(q, "네 자동으로 생성됩니다.", user2);
		answerRepository.save(a);
	}

	@Test
	@DisplayName("답변 조회하기")
	void t010() {
		Optional<Answer> oa = answerRepository.findById(1);
		assertTrue(oa.isPresent());
		Answer a = oa.get();
		assertEquals(2, a.getQuestion().getId());
	}

	@Transactional // 여기서의 트랜잭션의 역할 : 함수가 끝날 때까지 전화(DB와의)를 끊지 않음
	@Rollback(false)
	@Test
	@DisplayName("질문에 달린 답변 찾기")
	void t011() {
		Optional<Question> oq = this.questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		List<Answer> answerList = q.getAnswerList();

		assertEquals(1, answerList.size());
		assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
	}

	@Test
	@DisplayName("대량 테스트 데이터 만들기")
	void t012() {
		SiteUser user1 = userService.getUser("user1");

		for ( int i = 1; i <= 300; i++ ) {
			String subject = String.format("테스트 데이터 입니다.:[%03d]", i);
			String content = "내용무";
			this.questionService.create(subject, content, user1);
		}
	}


	@Test
	@DisplayName("스트림 버전 데이터 밀어넣기")
	void t013() {
		SiteUser user1 = userService.getUser("user1");

		IntStream.rangeClosed(3, 300)
				.forEach(no -> questionService.create("테스트 제목 입니다. %d".formatted(no),"테스트 내용입니다. %d".formatted(no), user1));
	}

	@Test
	@DisplayName("검색, 질문 제목으로 검색")
	void t014() {
		Page<Question> searchResult = questionService.getList(0, "sbb가 무엇인가요?");

		assertEquals(1, searchResult.getTotalElements());
	}

	@Test
	@DisplayName("검색, 질문 내용으로 검색")
	void t015() {
		Page<Question> searchResult = questionService.getList(0, "sbb에 대해서 알고 싶습니다.");

		assertEquals(1, searchResult.getTotalElements());
	}

	@Test
	@DisplayName("검색, 질문자 이름으로 검색")
	void t016() {
		Page<Question> searchResult = questionService.getList(0, "user2");

		assertEquals(1, searchResult.getTotalElements());
	}

	@Test
	@DisplayName("검색, 답변 내용으로 검색")
	void t017() {
		Page<Question> searchResult = questionService.getList(0, "네 자동으로 생성됩니다.");

		assertEquals(2, searchResult.getContent().get(0).getId());
		assertEquals(1, searchResult.getTotalElements());
	}

	@Test
	@DisplayName("검색, 답변자 이름으로 검색")
	void t018() {
		Page<Question> searchResult = questionService.getList(0, "user2");

		assertEquals(2, searchResult.getContent().get(0).getId());
		assertEquals(1, searchResult.getTotalElements());
	}
}