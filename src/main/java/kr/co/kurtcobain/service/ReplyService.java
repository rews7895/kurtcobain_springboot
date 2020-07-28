package kr.co.kurtcobain.service;

import kr.co.kurtcobain.domain.Board;
import kr.co.kurtcobain.domain.Reply;
import kr.co.kurtcobain.domain.User;
import kr.co.kurtcobain.repository.ReplyRepository;
import kr.co.kurtcobain.repository.UserRepository;
import kr.co.kurtcobain.security.CurrentUser;
import kr.co.kurtcobain.security.UserPrincipal;
import kr.co.kurtcobain.util.exception.ResourceNotFoundException;
import kr.co.kurtcobain.util.payload.Reply.ReplyRequest;
import kr.co.kurtcobain.util.payload.Reply.ReplyResponse;
import kr.co.kurtcobain.util.payload.board.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 댓글 관련 서비스 로직
 * 2020-07-19 PJS
 */
@RequiredArgsConstructor
@Service
public class ReplyService {

    private final UserRepository userRepository;

    private final ReplyRepository replyRepository;

    @Transactional
    public ReplyResponse save(UserPrincipal userPrincipal, ReplyRequest replyRequest) {

        // my
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        // toUser
        User toUser;
        if(replyRequest.getToUserId() != null) {
            toUser = userRepository.findById(replyRequest.getToUserId()).orElseThrow(
                    () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        } else {
            toUser = user;
        }

        Reply reply = replyRepository.save(Reply.builder()
                .boardId(replyRequest.getBoardId())
                .content(replyRequest.getContent())
                .originId(replyRequest.getOriginId())
                .user(user)
                .toUser(toUser)
                .build());

        // 댓글에서의 원글 처리
        if(replyRequest.getOriginId() == null) {
            // 없으면 자기 자신
            reply.originSave(reply.getId());
        } else {
            // 있으면 원글에 카운트를 +1하고 내려 받은 후 자기자신도 카운트 세팅
            Reply originReply = replyRepository.findById(reply.getOriginId()).orElseThrow(
                    () -> new ResourceNotFoundException("Reply", "id", userPrincipal.getId()));
            originReply.addDepth(originReply.getDepth());
            reply.setDepth(originReply.getDepth());
        }

        return new ReplyResponse(reply);
    }

    @Transactional
    public List<ReplyResponse> replies(Long boardId) {

        List<Reply> beforeReplies = replyRepository.findAllASC(boardId);

        return beforeReplies.stream()
                .map(ReplyResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public ReplyResponse update(Long id, ReplyRequest replyRequest, UserPrincipal userPrincipal) {

        // my
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        // 해당 댓글의 소유자가 요청 유저인지 확인
        Reply reply = replyRepository.findByIdAndUserAndDeletedDateIsNull(id, user);

        if(reply == null) {
            throw new ResourceNotFoundException("Reply", "id", id);
        }

        reply.update(replyRequest);

        return new ReplyResponse(reply);
    }

    @Transactional
    public void delete(Long id, UserPrincipal userPrincipal) {
        // my
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        // 해당 댓글의 소유자가 요청 유저인지 확인
        Reply reply = replyRepository.findByIdAndUserAndDeletedDateIsNull(id, user);

        if(reply == null) {
            throw new ResourceNotFoundException("Reply", "id", id);
        }

        reply.delete();
    }
}
