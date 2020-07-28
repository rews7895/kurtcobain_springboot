package kr.co.kurtcobain.domain;

import kr.co.kurtcobain.util.payload.Reply.ReplyRequest;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "replies")
@NoArgsConstructor
@Data
public class Reply {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long boardId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToOne
    private User user;

    // 원글 번호
    @Column
    private Long originId;

    // 원댓글에 달려있는 답글 개수
    @Column
    private int depth;

    @OneToOne
    private User toUser;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @Column
    private LocalDateTime deletedDate;

    @Builder
    public Reply(Long boardId, String content, User user, Long originId, User toUser) {

        this.boardId = boardId;
        this.content = content;
        this.user = user;
        this.originId = originId;
        this.toUser = toUser;
        this.depth = 0;
        this.createdDate = LocalDateTime.now();
    }

    public void originSave(Long id) {

        this.originId = id;
    }

    public void addDepth(int depth) {

        this.depth = depth + 1;
    }

    public void update(ReplyRequest replyRequest) {

        this.content = replyRequest.getContent();
        this.updatedDate = LocalDateTime.now();
    }

    public void delete() {

        this.deletedDate = LocalDateTime.now();
    }
}
