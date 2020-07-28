package kr.co.kurtcobain.util.payload.board;

import lombok.Data;

@Data
public class CreateResponse {
    private String variant;
    private String message;
    private Long id;

    public CreateResponse(String message, Long id) {
        this.variant = "success";
        this.message = message;
        this.id = id;
    }
}
