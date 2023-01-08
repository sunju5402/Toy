package com.example.toyservice.components;

import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MailComponents {
	private final JavaMailSender javaMailSender;

	public boolean sendMail(String mail, String subject, String text) {

		boolean result = false;
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
			result = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return result;
	}
}
