package kr.co.kurtcobain.util.payload.Reply;

import lombok.Data;

import java.util.List;

@Data
public class ReplyListResponse {
    List<ReplyResponse> replies;

    public ReplyListResponse(List<ReplyResponse> replies) {
        this.replies = replies;
    }
}
