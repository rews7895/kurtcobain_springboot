package kr.co.kurtcobain.service;

import kr.co.kurtcobain.domain.Board;
import kr.co.kurtcobain.domain.User;
import kr.co.kurtcobain.repository.BoardRepository;
import kr.co.kurtcobain.repository.UserRepository;
import kr.co.kurtcobain.security.UserPrincipal;
import kr.co.kurtcobain.util.exception.ResourceNotFoundException;
import kr.co.kurtcobain.util.payload.board.*;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/*
 * 자유게시판 관련 서비스 로직들
 * 2020-06-29 PJS
 */
@RequiredArgsConstructor
@Service
public class BoardService {
    private final UserRepository userRepository;

    private final BoardRepository boardRepository;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    // 게시글 저장
    @Transactional
    public Long save(UserPrincipal userPrincipal, CreateRequest createRequest) {
        // Given
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        Long id = boardRepository.save(Board.builder()
                .user(user)
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .build()).getId();
        return id;
    }
    
    // 게시글 상세페이지
    @Transactional
    public Board boardDetail(Long id) {
        Board board = boardRepository.findByIdAndDeletedDateIsNull(id);
        if(board == null) {
            throw new ResourceNotFoundException("Board", "id", id);
        }
        board.addHit(board.getHit());

        return board;
    }

    // lucene기반 full text 검색(제목과 컨텐츠)
    public ListResponse boards(String keyword, Pageable pageable) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
                .forEntity(Board.class)
                .get();
        Query query;
        // domain의 @SortableField
        // ORDER BY createdDate DESC
        Sort sort = queryBuilder.sort().byField("createdDate").desc().createSort();
        // ""일때는 키워드가 없어 EmptyQueryException발생으로 공백으로 들어올 때 전체 쿼리 출력
        // 삭제 시 deletedDate에 날짜가 입력되므로 삭제가 안된 것들만 찾아서 검색(2020-07-17)
        if(keyword.equals("")) {
            query = queryBuilder.keyword().onField("deletedDate").matching(null).createQuery();
        } else {
            query = queryBuilder.bool()
                    .must(queryBuilder
                            .keyword()
                            .onFields("title", "content")
                            .matching(keyword)
                            .createQuery())
                    .must(queryBuilder
                            .keyword()
                            .onField("deletedDate")
                            .matching(null)
                            .createQuery())
                    .createQuery();
        }

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, Board.class);
        fullTextQuery
                .setSort(sort)
                .setFirstResult(pageable.getPageSize() * pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize());
        List<Board> boards = (List<Board>) fullTextQuery.getResultList();

        List<BoardResponse> result = boards.stream()
                .map(BoardResponse::new).collect(Collectors.toList());
        int total = fullTextQuery.getResultSize();
        int totalPage = (int) Math.ceil((double)total / (double)10);
        PagingResponse pagingResponse = new PagingResponse(total, totalPage, pageable.getPageNumber() + 1, keyword);
        return new ListResponse(result, pagingResponse);
    }
    
    // 게시글 수정
    @Transactional
    public Long update(Long id, UserPrincipal userPrincipal, CreateRequest createRequest) {
        // 게시글의 소유주인지 확인하고 맞으면 수정 아니면 반려
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        // 게시글의 소유주로써 수정권한이 있으면 해당 게시물을 불러온다.
        Board board = boardRepository.findByIdAndUser(id, user);
        // 게시글이 없으면 에러처리
        if(board == null) {
            throw new ResourceNotFoundException("Board", "id", id);
        }
        // 최종 수정
        board.update(createRequest);

        return board.getId();
    }

    @Transactional
    public void delete(Long id, UserPrincipal userPrincipal) {
        // 게시글의 소유주인지 확인하고 맞으면 삭제 아니면 반려
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        // 게시글의 소유주로써 삭제권한이 있으면 해당 게시물을 불러온다.
        Board board = boardRepository.findByIdAndUser(id, user);
        // 게시글이 없으면 에러처리
        if(board == null) {
            throw new ResourceNotFoundException("Board", "id", id);
        }
        // 최종 삭제
        board.delete();
    }

}
