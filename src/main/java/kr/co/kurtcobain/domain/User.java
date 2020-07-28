package kr.co.kurtcobain.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.kurtcobain.domain.enums.AuthProvider;
import kr.co.kurtcobain.domain.enums.Role;
import lombok.Builder;
import lombok.Data;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/*
 * User Entity 명세
 * 2020-06-16 PJS
 * 1. id외에 id로 사용하는 email에 대한 unique 추가(2020-06-16)
 * 2. emailVerified은 일반 회원가입 시 이메일 인증 기능을 넣을 시 대비
 * 3. ROLE_USER 유저권한 분리
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field
    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false)
    private String email;

    private String imageUrl;

    // 이메일 인증(일반가입 받는 유저 사용 시...)
    @JsonIgnore
    @Column(nullable = false)
    private Boolean emailVerified = true;

    @JsonIgnore
    private String password;

    // 공급자 ex) naver, kakao...
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    // 공급자에서 받은 고유 id값
    @JsonIgnore
    private String providerId;

    @JsonIgnore
    @Column(nullable = false)
    private LocalDateTime createdDatetime = LocalDateTime.now();

    @JsonIgnore
    private LocalDateTime updatedDatetime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_USER;

}

/*
 * @JsonIgnore: JSON 응답내릴 때 Java Bean 클래스 특정 변수(필드) SKIP 하는 방법.
 */