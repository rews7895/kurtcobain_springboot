package kr.co.kurtcobain.security.oauth2.user;

import java.util.Map;

/*
 * OAuth2 공급자에 의한 인증 된 사용자의 세부 정보를 가져올 json응답을 가져올 추상 클래스
 * 2020-06-17 PJS
 */
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();
}

/* NOTE
 추상 클래스(abstract class)란 하나 이상의 추상 메소드(abstract method)를 포함하는 클래스이다.
 추상 메소드는 선언만 있고 본체는 없는 함수이며 선언부에 ‘abstract’ 라는 키워드를 붙인다.
 추상 메소드가 포함되었다면 클래스도 추상 클래스이므로 클래스명 앞에도 ‘abstract’키워드를 붙여야 한다.
 */
