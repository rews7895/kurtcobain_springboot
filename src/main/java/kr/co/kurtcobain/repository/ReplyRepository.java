package kr.co.kurtcobain.repository;

import kr.co.kurtcobain.domain.Reply;
import kr.co.kurtcobain.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*
 * 댓글 리포지토리
 * 2020-07-20 PJS
 */
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 해당 게시글에 달려있는 댓글 모두를 조회(삭제 제외, originId ASC, id ASC)
    @Query("SELECT p FROM Reply p WHERE p.boardId = :boardId AND p.deletedDate IS NULL ORDER BY p.originId ASC, p.id ASC")
    List<Reply> findAllASC(@Param("boardId") Long boardId);

    // 해당 댓글의 권한이 있는 유저를 검색조건에 같이 넣고, 삭제되지 않은 댓글만 조회 후 처리
    Reply findByIdAndUserAndDeletedDateIsNull(Long id, User user);
}
