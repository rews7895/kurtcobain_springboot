package kr.co.kurtcobain.repository;

import kr.co.kurtcobain.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 * 데이터베이스의 데이터에 액세스하기 위한 저장소 계층(User Entity에 대한 데이터베이스 접근 제공)
 * 2020-06-16 PJS
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
