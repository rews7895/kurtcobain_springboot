package kr.co.kurtcobain.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.lang.annotation.*;

/*
 * 컨트롤러에 현재 인증 된 사용자 principal 주입하는 데 사용
 * 2020-06-17 PJS
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
