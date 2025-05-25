package com.readyvery.readyverydemo.src.order;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StoreSseEmitterManager {

	// storeId -> SseEmitter 목록
	private final Map<Long, List<SseEmitter>> storeEmitterMap = new ConcurrentHashMap<>();

	/**
	 * 특정 가게에 대한 SSE Emitter를 생성하고 등록.
	 */
	public SseEmitter createEmitterForStore(Long storeId) {
		// 타임아웃 60초(60000L) 예시
		SseEmitter emitter = new SseEmitter(60000L);

		// storeEmitterMap에서 storeId 키가 없으면 새 ArrayList 생성 후 emitter 추가
		storeEmitterMap.computeIfAbsent(storeId, id -> new CopyOnWriteArrayList<>())
			.add(emitter);

		// SSE 완료/타임아웃/에러 시 삭제 처리
		emitter.onCompletion(() -> removeEmitter(storeId, emitter));
		emitter.onTimeout(() -> removeEmitter(storeId, emitter));
		emitter.onError((ex) -> removeEmitter(storeId, emitter));

		return emitter;
	}

	private void removeEmitter(Long storeId, SseEmitter emitter) {
		List<SseEmitter> emitterList = storeEmitterMap.get(storeId);
		if (emitterList != null) {
			emitterList.remove(emitter);
			// 빈 리스트가 되면 맵에서 제거할 수도 있음
			if (emitterList.isEmpty()) {
				storeEmitterMap.remove(storeId);
			}
		}
	}

	/**
	 * 특정 가게(storeId)에만 이벤트를 전송
	 */
	public void sendEventToStore(Long storeId, Object event) {
		List<SseEmitter> emitterList = storeEmitterMap.get(storeId);
		if (emitterList == null) {
			return; // 해당 storeId로 구독 중인 Emitter가 없음
		}

		for (SseEmitter emitter : emitterList) {
			try {
				// 이벤트명 "orderUpdate"로 설정
				emitter.send(SseEmitter.event().name("orderUpdate").data(event));
			} catch (IOException e) {
				log.warn("[StoreSseEmitterManager] sendEventToStore IOException: {}", e.getMessage());
				emitter.completeWithError(e);
				emitterList.remove(emitter);
			}
		}
	}
}

