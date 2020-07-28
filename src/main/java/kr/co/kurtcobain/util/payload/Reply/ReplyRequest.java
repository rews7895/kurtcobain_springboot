package kr.co.kurtcobain.util.payload.Reply;

import kr.co.kurtcobain.domain.User;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*
 * 댓글 등록 요청 DTO
 * 2020-07-19 PJS
 * React에서는 content에 대해서만 에러가 있을 시 메세지를 보여주고 이외의 것들은 실패했다는 메시지 출력으로 처리
 */
@Data
public class ReplyRequest {

    @NotNull
    private Long boardId;

    @Size(min = 1, max = 100, message = "댓글은 1자 이상 100자 이하까지 허용됩니다.")
    private String content;

    private Long originId;

    private Long toUserId;
}
