package kr.co.kurtcobain.util.payload.board;

import lombok.Data;

@Data
public class PagingResponse {
    int total;
    int totalPage;
    int currentPage;
    String keyword;

    public PagingResponse(int total, int totalPage, int pageNumber, String keyword) {
        this.total = total;
        this.totalPage = totalPage;
        this.currentPage = pageNumber;
        this.keyword = keyword;
    }
}
