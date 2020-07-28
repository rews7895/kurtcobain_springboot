package kr.co.kurtcobain.util.payload.board;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.*;

/*
 * 게시글 작성 관련 요청
 * 2020-06-29 PJS
 */
@Data
//@RequiredArgsConstructor
public class CreateRequest {

    @NotBlank(message = "제목은 공백을 허용하지 않습니다.")
    @Size(min = 5, max = 100, message = "제목은 5 ~ 100자까지 가능합니다.")
    private String title;

    //<p></p> 기본 7자
    @NotBlank(message = "게시글은 공백을 허용하지 않습니다.")
    @Size(min = 12, max = 5000, message = "게시글은 최소 5글자 이상 ~ 5000자(태그포함) 이하입니다.")
    private String content;

}
