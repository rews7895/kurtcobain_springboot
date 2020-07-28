package kr.co.kurtcobain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "files")
@Data
public class BoardFile implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @NotEmpty
    @Column(nullable = false)
    private String originalFileName;

    @NotEmpty
    @Column(nullable = false)
    private String path;

    @JsonIgnore
    @Column(nullable = false)
    private Long size;

    @JsonIgnore
    @OneToOne
    private User user;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean useCheck;

    @JsonIgnore
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @JsonIgnore
    private LocalDateTime updatedDate;

    @Builder
    public BoardFile(String originalFileName, String path, Long size, User user) {
        this.originalFileName = originalFileName;
        this.path = path;
        this.size = size;
        this.user = user;
        this.useCheck = false;
        this.createdDate = LocalDateTime.now();
    }
}
