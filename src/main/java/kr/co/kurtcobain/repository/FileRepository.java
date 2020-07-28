package kr.co.kurtcobain.repository;

import kr.co.kurtcobain.domain.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
 * 파일 관련 리포지토리
 * 2020-07-03 PJS
 */
public interface FileRepository extends JpaRepository<BoardFile, Long> {

}
