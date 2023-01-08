package com.example.toyservice.components;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MailComponentsTest {


	@Mock
	private JavaMailSender javaMailSender;

	@Test
	public void sendMailTest() {
	    // given
		SimpleMailMessage msg = new SimpleMailMessage();

	    // when

		msg.setTo("sunju5402@naver.com");
		msg.setSubject("안녕하세요");
		msg.setText("안녕하세요. 토이서비스입니다.");

		javaMailSender.send(msg);

	    // then
	}

}