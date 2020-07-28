package kr.co.kurtcobain.repository;

import kr.co.kurtcobain.domain.Board;
import kr.co.kurtcobain.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * 자유게시판 관련 리포지토리
 * 2020-06-29 PJS
 */
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByIdAndUser(Long id, User user);
    Board findByIdAndDeletedDateIsNull(Long id);
}
