package com.example.toyservice.components;

import com.example.toyservice.ToyServiceApplication;
import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.constants.ErrorCode;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MailComponents {
	private final JavaMailSender javaMailSender;
	private static final Logger logger = LoggerFactory.getLogger(ToyServiceApplication.class);

	public void sendMail(String mail, String subject, String text) {
		MimeMessagePreparator msg = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true,
					"UTF-8");
				mimeMessageHelper.setTo(mail);
				mimeMessageHelper.setSubject(subject);
				mimeMessageHelper.setText(text, true);
			}
		};

		try { // rollback할 방법이 없어서 try-catch 사용
			javaMailSender.send(msg);
		} catch (Exception e) {
			throw new AuthenticationException(ErrorCode.EMAIL_SEND_FAIL);
		}
	}
}
