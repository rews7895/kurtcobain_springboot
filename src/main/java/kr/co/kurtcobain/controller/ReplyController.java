package kr.co.kurtcobain.controller;

import kr.co.kurtcobain.security.CurrentUser;
import kr.co.kurtcobain.security.UserPrincipal;
import kr.co.kurtcobain.service.ReplyService;
import kr.co.kurtcobain.util.ErrorsResponse;
import kr.co.kurtcobain.util.payload.Reply.ReplyListResponse;
import kr.co.kurtcobain.util.payload.Reply.ReplyRequest;
import kr.co.kurtcobain.util.payload.Reply.ReplyResponse;
import kr.co.kurtcobain.util.payload.Reply.ReplySuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ReplyController {

    private final ReplyService replyService;

    // 댓글 생성
    @PostMapping("/reply")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> saveReply(@CurrentUser UserPrincipal userPrincipal, @RequestBody @Valid ReplyRequest replyRequest, Errors errors) {

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResponse(errors));
        }

        ReplyResponse reply = replyService.save(userPrincipal, replyRequest);
        return ResponseEntity.ok(new ReplySuccessResponse("저장에 성공했습니다.", reply));
    }

    // 댓글 리스트
    @GetMapping("/reply/{boardId}")
    public ResponseEntity<?> getReplies(@PathVariable("boardId") Long boardId) {

        List<ReplyResponse> replies = replyService.replies(boardId);
        return ResponseEntity.ok(new ReplyListResponse(replies));
    }

    // 댓글 수정
    @PatchMapping("/reply/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateReply(@PathVariable("id") Long id, @CurrentUser UserPrincipal userPrincipal, @RequestBody @Valid ReplyRequest replyRequest, Errors errors) {

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResponse(errors));
        }

        ReplyResponse reply = replyService.update(id, replyRequest, userPrincipal);
        return ResponseEntity.ok(new ReplySuccessResponse("수정에 성공했습니다.", reply));
    }

    // 댓글 삭제
    @DeleteMapping("/reply/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteReply(@PathVariable("id") Long id, @CurrentUser UserPrincipal userPrincipal) {

        replyService.delete(id, userPrincipal);
        return ResponseEntity.ok(new ReplySuccessResponse("삭제에 성공했습니다.", null));
    }
}
