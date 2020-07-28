package kr.co.kurtcobain.util.payload.board;

import lombok.Data;

import java.util.List;

@Data
public class ListResponse {
    List<BoardResponse> boards;
    PagingResponse info;

    public ListResponse(List<BoardResponse> boards, PagingResponse info) {
        this.boards = boards;
        this.info = info;
    }
}
