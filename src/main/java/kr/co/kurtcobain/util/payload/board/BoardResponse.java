package kr.co.kurtcobain.util.payload.board;

import kr.co.kurtcobain.domain.Board;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponse{
    private Long id;

    private String title;

    private String content;

    private Long hit;

    private String userName;

    private Long userId;

    private LocalDateTime createdDate;

    private LocalDateTime deletedDate;

    public BoardResponse(Board freeBoard) {
        this.id = freeBoard.getId();
        this.title = freeBoard.getTitle();
        this.content = freeBoard.getContent();
        this.userName = freeBoard.getUser().getName();
        this.createdDate = freeBoard.getCreatedDate();
        this.deletedDate = freeBoard.getDeletedDate();
        this.hit = freeBoard.getHit();
        this.userId = freeBoard.getUser().getId();
    }
}
