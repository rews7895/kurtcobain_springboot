package kr.co.kurtcobain.util.payload.Reply;

import kr.co.kurtcobain.domain.Reply;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ReplyResponse {

    private Long id;

    private Long boardId;

    private String content;

    private Long originId;

//    private int group;
//
//    private int depth;

    private Long userId;

    private String userName;

    private Long toUserId;

    private String toUserName;

    private String createdDate;

    public ReplyResponse(Reply reply) {
        this.id = reply.getId();
        this.boardId = reply.getBoardId();
        this.content = reply.getContent();
        this.originId = reply.getOriginId();
        this.userId = reply.getUser().getId();
        this.userName = reply.getUser().getName();
        this.toUserId = reply.getToUser().getId();
        this.toUserName = reply.getToUser().getName();
        this.createdDate = reply.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
