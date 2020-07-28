package kr.co.kurtcobain.util.payload;

import lombok.Data;

@Data
public class TokenResponse {
    private String accessToken;
    private String expires;

    public TokenResponse(String accessToken, String expires) {
        this.accessToken = accessToken;
        this.expires = expires;
    }
}
