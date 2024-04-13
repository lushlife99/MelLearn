package com.example.melLearnBE;

import com.example.melLearnBE.enums.LearningLevel;
import com.example.melLearnBE.model.ListeningQuiz;
import com.example.melLearnBE.model.ListeningSubmit;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.repository.ListeningQuizRepository;
import com.example.melLearnBE.repository.ListeningSubmitRepository;
import com.example.melLearnBE.repository.MemberRepository;
import nu.xom.jaxen.util.SingletonList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.List;

@SpringBootTest
class MeLearnBeApplicationTests {

	@Autowired
	private ListeningSubmitRepository listeningSubmitRepository;
	@Autowired
	private ListeningQuizRepository listeningQuizRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	void contextLoads() {

		for(int i = 0; i < 21; i++) {
			Member member = memberRepository.findByMemberId("a1").get();

			ListeningQuiz listeningQuiz = ListeningQuiz.builder()
					.blankedText("asdfasdfasdf")
					.musicId("123")
					.level(LearningLevel.Beginner)
					.answerList(List.of("a", "b", "c"))
					.build();
			listeningQuiz = listeningQuizRepository.save(listeningQuiz);


			ListeningSubmit listeningSubmit = ListeningSubmit.builder()
					.score(100)
					.listeningQuiz(listeningQuiz)
					.member(member)
					.level(LearningLevel.Beginner)
					.build();
			listeningSubmit = listeningSubmitRepository.save(listeningSubmit);

			listeningQuiz.setSubmitList(List.of(listeningSubmit));


		}

	}

}
