package kr.co.kurtcobain.util.payload.Reply;

import lombok.Data;

@Data
public class ReplySuccessResponse {
    private String variant;
    private String message;
    private ReplyResponse reply;

    public ReplySuccessResponse(String message, ReplyResponse replyResponse) {
        this.variant = "success";
        this.message = message;
        this.reply = replyResponse;
    }
}
