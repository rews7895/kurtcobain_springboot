package kr.co.kurtcobain.util.payload.board;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/*
 * 게시글 작성 관련 요청
 * 2020-06-29 PJS
 */
@Data
@RequiredArgsConstructor
public class CreateBadResponse {
    private String field;
    private String defaultMessage;
}
