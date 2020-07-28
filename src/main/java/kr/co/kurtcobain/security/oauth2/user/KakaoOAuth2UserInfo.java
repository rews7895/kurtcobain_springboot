package kr.co.kurtcobain.security.oauth2.user;

import java.util.Map;

/*
 * 카카오 유저 정보
 * 2020-06-17 PJS
 * 1. properties값은 호출 시 무조건 불러올 수 있는 값으로 nickname, thumbnail_image의 값을 properties에 있는 값으로 사용 (2020-06-24)
 * 2. 카카오 로그인 유저정보는 이메일이 필수가 아니라서(검수 전) 사업자 등록이 안되도 검수가 가능하면 검수 후 필수 항목으로 등록 필요 (2020-06-24)
 */
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getName() {
        return (String) properties().get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) kakao_account().get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) properties().get("thumbnail_image");
    }

    public Map<String, Object> kakao_account() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }
    public Map<String, Object> properties() {
        return (Map<String, Object>) attributes.get("properties");
    }
}
