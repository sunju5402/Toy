package com.example.toyservice.components;

import java.util.HashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketHandler extends TextWebSocketHandler {

	HashMap<String, WebSocketSession> sessionMap = new HashMap<>();

	/**
	 * 메세지 수신시, 실행
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message)
		throws Exception {
		String msg = message.getPayload();
		for(String key : sessionMap.keySet()) {
			WebSocketSession wss = sessionMap.get(key);
			try {
				wss.sendMessage(new TextMessage(msg));
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 소켓 연결
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessionMap.put(session.getId(), session);
	}

	/**
	 * 소켓 종료
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
		throws Exception {
		sessionMap.remove(session.getId());
	}
}
