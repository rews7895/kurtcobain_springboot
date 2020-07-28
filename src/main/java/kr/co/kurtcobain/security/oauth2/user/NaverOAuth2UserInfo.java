package kr.co.kurtcobain.security.oauth2.user;

import java.util.Map;

/*
 * 네이버 유저 정보
 * 2020-06-17 PJS
 * 1. 아래 Override 수정 필요 (수정 완료 2020-06-24)
 * 2. 네이버의 유저정보는 attributes 아래 response에 있음
 */
public class NaverOAuth2UserInfo extends OAuth2UserInfo {
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {

        super((Map<String, Object>) attributes.get("response"));
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("profile_image");
    }
}
