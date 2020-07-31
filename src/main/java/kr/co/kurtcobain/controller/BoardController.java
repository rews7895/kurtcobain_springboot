package kr.co.kurtcobain.controller;

import kr.co.kurtcobain.domain.Board;
import kr.co.kurtcobain.security.CurrentUser;
import kr.co.kurtcobain.security.UserPrincipal;
import kr.co.kurtcobain.service.BoardService;

import kr.co.kurtcobain.util.ErrorsResponse;
import kr.co.kurtcobain.util.payload.board.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.Validator;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;

    // 게시글 생성
    @PostMapping("/board")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> saveFreeBoard(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody CreateRequest createRequest, Errors errors) {

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResponse(errors));
        }

        Long id = boardService.save(userPrincipal, createRequest);

        return ResponseEntity.ok(new CreateResponse("저장에 성공했습니다.", id));
    }

    // 게시글 상세
    @GetMapping("/board/{id}")
    public ResponseEntity<?> getFreeBoard(@PathVariable("id") Long id) {

        Board freeBoard = boardService.boardDetail(id);

        return ResponseEntity.ok(new BoardResponse(freeBoard));
    }

    // 게시글 리스트
    @GetMapping("/board")
    public ResponseEntity<?> getBoards(@PageableDefault Pageable pageable, @RequestParam(defaultValue = "") String keyword) {

        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize());
        ListResponse data = boardService.boards(keyword, pageable);

        return ResponseEntity.ok(data);
    }

    // 게시글 수정
    @PatchMapping("/board/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateBoard(@CurrentUser UserPrincipal userPrincipal, @PathVariable("id") Long id, @Valid @RequestBody CreateRequest createRequest, Errors errors) {

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResponse(errors));
        }

        Long boardId = boardService.update(id, userPrincipal, createRequest);

        return ResponseEntity.ok(new CreateResponse("수정에 성공했습니다.", boardId));
    }

    // 게시글 삭제
    @DeleteMapping("/board/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteBoard(@PathVariable("id") Long id, @CurrentUser UserPrincipal userPrincipal) {

        boardService.delete(id, userPrincipal);

        return ResponseEntity.ok(new CreateResponse("삭제에 성공했습니다.", null));
    }
}
