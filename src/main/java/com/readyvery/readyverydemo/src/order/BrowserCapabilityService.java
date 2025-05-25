package com.readyvery.readyverydemo.src.order;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class BrowserCapabilityService {

	private static final int MIN_CHROMIUM_VERSION_FOR_SSE = 90;
	private static final int MIN_FIREFOX_VERSION_FOR_SSE = 6;
	private static final int MIN_SAFARI_VERSION_FOR_SSE = 5;

	private static final Pattern CHROMIUM_CH_PATTERN =
		Pattern.compile("(Chromium|Google Chrome)\"?;v=\"?(\\d+)");
	private static final Pattern FIREFOX_CH_PATTERN =
		Pattern.compile("(Firefox)\"?;v=\"?(\\d+)");
	private static final Pattern SAFARI_CH_PATTERN =
		Pattern.compile("(Safari)\"?;v=\"?(\\d+)");

	private static final Pattern CHROME_UA_PATTERN =
		Pattern.compile("Chrome/(\\d+)");

	private static final Pattern FIREFOX_UA_PATTERN =
		Pattern.compile("Firefox/(\\d+)");

	private static final Pattern SAFARI_VERSION_UA_PATTERN =
		Pattern.compile("Version/(\\d+).*Safari/");

	/**
	 * Client Hints와 User-Agent 정보를 종합해서 SSE 지원 여부를 판별.
	 */
	public boolean isSseCapableBrowser(HttpServletRequest request) {
		// 1) 우선 Sec-CH-UA (Client Hints)를 확인
		String secChUa = request.getHeader("Sec-CH-UA");
		if (secChUa != null) {
			// Chromium/Chrome
			Integer chromiumVersion = parseVersionFromCh(secChUa, CHROMIUM_CH_PATTERN);
			if (chromiumVersion != null && chromiumVersion >= MIN_CHROMIUM_VERSION_FOR_SSE) {
				return true;
			}

			// Firefox
			Integer firefoxVersion = parseVersionFromCh(secChUa, FIREFOX_CH_PATTERN);
			if (firefoxVersion != null && firefoxVersion >= MIN_FIREFOX_VERSION_FOR_SSE) {
				return true;
			}

			// Safari
			Integer safariVersion = parseVersionFromCh(secChUa, SAFARI_CH_PATTERN);
			if (safariVersion != null && safariVersion >= MIN_SAFARI_VERSION_FOR_SSE) {
				return true;
			}
		}

		// 2) Client Hints가 없거나 파싱 실패 → User-Agent로 폴백
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null) {
			return false;
		}

		// 구형 IE (Trident, MSIE) → SSE 미지원
		if (userAgent.contains("Trident") || userAgent.contains("MSIE")) {
			return false;
		}

		// Firefox 판별
		Matcher fxMatcher = FIREFOX_UA_PATTERN.matcher(userAgent);
		if (fxMatcher.find()) {
			int fxVersion = Integer.parseInt(fxMatcher.group(1));
			return fxVersion >= MIN_FIREFOX_VERSION_FOR_SSE;
		}

		// Safari 판별
		// Safari UA 문자열엔 "Safari"가 포함되지만
		// Chrome 계열에도 "Safari" 문자열이 들어갈 수 있으므로
		// "Chrome/" 또는 "Chromium"이 있는지 먼저 확인이 필요
		if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
			Matcher safariMatcher = SAFARI_VERSION_UA_PATTERN.matcher(userAgent);
			if (safariMatcher.find()) {
				int safariVersion = Integer.parseInt(safariMatcher.group(1));
				return safariVersion >= MIN_SAFARI_VERSION_FOR_SSE;
			}
			// Safari 문자열은 있는데 Version/이 없으면 구버전이거나 별도 케이스
			// 일단 미지원으로 처리
			return false;
		}

		// 2-4) Chromium/Chrome 판별
		Matcher chromeMatcher = CHROME_UA_PATTERN.matcher(userAgent);
		if (chromeMatcher.find()) {
			int chromeVersion = Integer.parseInt(chromeMatcher.group(1));
			return chromeVersion >= MIN_CHROMIUM_VERSION_FOR_SSE;
		}

		// 3) 그 외 오페라, Edge(Chromium), 기타 브라우저 등은 별도 처리 가능
		return false;
	}

	/**
	 * Sec-CH-UA 문자열에서 특정 브랜드(Chromium, Firefox, Safari 등) 버전을 찾는 메서드
	 */
	private Integer parseVersionFromCh(String secChUa, Pattern pattern) {
		Matcher matcher = pattern.matcher(secChUa);
		if (matcher.find()) {
			try {
				return Integer.parseInt(matcher.group(2));
			} catch (NumberFormatException ignored) {
			}
		}
		return null;
	}
}
