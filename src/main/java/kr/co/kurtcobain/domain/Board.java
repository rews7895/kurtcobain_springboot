package kr.co.kurtcobain.domain;

import kr.co.kurtcobain.util.payload.board.CreateRequest;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.*;
import org.apache.lucene.analysis.ko.KoreanFilterFactory;
import org.apache.lucene.analysis.ko.KoreanTokenizerFactory;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "boards")
@NoArgsConstructor
@Data
@Indexed
@AnalyzerDef(name = "koreanAnalyzerFreeBoard"
        , tokenizer = @TokenizerDef(factory = KoreanTokenizerFactory.class)
        , filters = { @TokenFilterDef(factory = KoreanFilterFactory.class)})
public class Board implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field
    @Analyzer(definition = "koreanAnalyzerFreeBoard")
    @Column(nullable = false)
    private String title;

    @Field
    @Analyzer(definition = "koreanAnalyzerFreeBoard")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(nullable = false)
    private Long hit;

    @IndexedEmbedded
    @OneToOne
    private User user;

    @Field( analyze = Analyze.NO, store = Store.YES)
    @SortableField
    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @Field( analyze = Analyze.NO, indexNullAs = "2999-12-31")
    @Column
    private LocalDateTime deletedDate;

    @Builder
    public Board(String title, String content, User user) {

        this.title = title;
        this.content = content;
        this.user = user;
        this.createdDate = LocalDateTime.now();
        this.hit = Long.valueOf(0);
    }

    public void addHit(Long hit) {

        this.hit = hit + 1;
    }

    public void update(CreateRequest createRequest) {

        this.title = createRequest.getTitle();
        this.content = createRequest.getContent();
        this.updatedDate = LocalDateTime.now();
    }

    public void delete() {

        this.deletedDate = LocalDateTime.now();
    }
}
