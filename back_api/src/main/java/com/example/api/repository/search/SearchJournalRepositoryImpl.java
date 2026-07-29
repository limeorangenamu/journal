package com.example.api.repository.search;

import com.example.api.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor // JPAQueryFactory 주입을 위한 생성자 자동 생성
public class SearchJournalRepositoryImpl implements SearchJournalRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Object[]> searchPage(String type, String keyword, Pageable pageable) {
        // 1) 도메인 선언
        QJournal qJournal = QJournal.journal;
        QPhotos qPhotos = QPhotos.photos;
        QPhotos qPhotosSub = QPhotos.photos;
        QMembers qMembers = QMembers.members;
        QComments qComments = QComments.comments;

        // 2) 검색 조건 객체 생성 및 기본 조건 지정
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qJournal.jno.gt(0L)); // 기본 검색 조건

        // 3) 검색 조건 추가
        if (type != null && !type.trim().isEmpty()) {
            String[] typeArr = type.split("");
            BooleanBuilder condition = new BooleanBuilder();
            for (String t : typeArr) {
                switch (t) {
                    case "t" -> condition.or(qJournal.title.containsIgnoreCase(keyword));
                    case "w" -> condition.or(qMembers.email.containsIgnoreCase(keyword));
                    case "c" -> condition.or(qJournal.content.containsIgnoreCase(keyword));
                }
            }
            builder.and(condition);
        }

        // 4) 본 쿼리 객체 생성 (JPAQueryFactory 활용)
        JPAQuery<Tuple> query = queryFactory
                .select(
                        qJournal,
                        qPhotos,
                        qMembers,
                        qComments.likes.sum().coalesce(0L),
                        qComments.countDistinct()
                )
                .from(qJournal)
                .leftJoin(qPhotos).on(
                        qPhotos.journal.eq(qJournal)
                                .and(qPhotos.pno.eq(
                                        JPAExpressions
                                                .select(qPhotosSub.pno.max())
                                                .from(qPhotosSub)
                                                .where(qPhotosSub.journal.eq(qJournal))
                                ))
                )
                .leftJoin(qMembers).on(qJournal.members.eq(qMembers))
                .leftJoin(qComments).on(qComments.journal.eq(qJournal))
                .where(builder)
                .groupBy(qJournal.jno, qPhotos.pno, qMembers.mid);

        // 5) 정렬 조건 추가
        Sort sort = pageable.getSort();
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder orderByExpression = new PathBuilder(Journal.class, "journal");
            query.orderBy(
                    new OrderSpecifier<>(
                            direction,
                            orderByExpression.get(order.getProperty())
                    )
            );
        });

        // 6) 데이터 카운트 조회 (페이징용 총 개수)
        Long count = queryFactory
                .select(qJournal.countDistinct())
                .from(qJournal)
                .leftJoin(qPhotos).on(
                        qPhotos.journal.eq(qJournal)
                                .and(qPhotos.pno.eq(
                                        JPAExpressions
                                                .select(qPhotosSub.pno.min())
                                                .from(qPhotosSub)
                                                .where(qPhotosSub.journal.eq(qJournal))
                                ))
                )
                .leftJoin(qComments).on(qComments.journal.eq(qJournal))
                .leftJoin(qMembers).on(qComments.members.eq(qMembers))
                .where(builder)
                .fetchOne();
        if (count == null) count = 0L;
        log.info("총 개수 출력: " + count);

        // 7) 페이징 오프셋 및 제한 설정 후 데이터 fetch
        List<Tuple> result = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 8) Page 객체 변환 및 반환
        List<Object[]> content = result.stream()
                .map(Tuple::toArray)
                .toList();

        return new PageImpl<>(content, pageable, count);
    }
}

/*
package com.example.api.repository.search;

import com.example.api.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class SearchJournalRepositoryImpl extends QuerydslRepositorySupport implements SearchJournalRepository {
    public SearchJournalRepositoryImpl() {
        super(Journal.class);
    }

    @Override
    public Page<Object[]> searchPage(String type, String keyword, Pageable pageable) {
        // 1) 도메인 선언
        QJournal qJournal = QJournal.journal;
        QPhotos qPhotos = QPhotos.photos;
        QPhotos qPhotosSub = QPhotos.photos;
        QMembers qMembers = QMembers.members;
        QComments qComments = QComments.comments;

        // 2) 도메인을 조인
        JPQLQuery<Journal> jpqlQuery = from(qJournal);
        jpqlQuery.leftJoin(qPhotos).on(
                qPhotos.journal.eq(qJournal)
                        .and(
                                qPhotos.pno.eq(
                                        JPAExpressions
                                                .select(qPhotosSub.pno.min())
                                                .from(qPhotosSub)
                                                .where(qPhotosSub.journal.eq(qJournal))
                                )
                        )
        );
        jpqlQuery.leftJoin(qComments).on(qComments.journal.eq(qJournal));
        jpqlQuery.leftJoin(qMembers).on(qComments.members.eq(qMembers));

        // 3) Tuple 생성: 조인한 객체와 select를 이용해서 필요한 데이터를 tuple로 생성
        JPQLQuery<Tuple> tuple = jpqlQuery.select(
                qJournal, qPhotos, qComments.likes.sum().coalesce(0L), qComments.countDistinct()
        );

        // 4) 조건절 검색을 위한 검색 객체를 생성
        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression expression = qJournal.jno.gt(0l); // 기본 검색 조건
        builder.and(expression); // 필수 검색 조건 지정

        // 5) 검색 조건 추가
        if (type != null) {
            String[] typeArr = type.split(""); // 한글자씩 떼어서 배열 생성
            BooleanBuilder condition = new BooleanBuilder();
            for (String t : typeArr) {
                switch (t) {
                    case "t" -> condition.or(qJournal.title.containsIgnoreCase(keyword));
                    case "w" -> condition.or(qMembers.email.containsIgnoreCase(keyword));
                    default -> condition.or(qMembers.nickname.containsIgnoreCase(keyword));
                }
            }
            builder.and(condition);
        }

        // 6) 조인된 tuple에 추가된 조건절 적용
        tuple.where(builder);

        // 7) 조인된 데이터의 select를 위한 group by 설정
        //tuple.groupBy(qJournal); // MariaDB
        tuple.groupBy(qJournal, qPhotos);  //MySQL

        // 8) 정렬조건 추가
        Sort sort = pageable.getSort(); // pageable에서 정렬 정보를 가져온다.
        sort.stream().forEach( order -> {  // 하나씩 거내본다.
            Order direction = order.isAscending()? Order.ASC : Order.DESC; // 정렬객체 지정
            PathBuilder orderByExpression = new PathBuilder(Journal.class, "journal");
            tuple.orderBy(
                    new OrderSpecifier<>(
                            direction,
                            orderByExpression.get(order.getProperty())
                    )
            );
        });

        // 9) tuple의 데이터를 가져오기 위한 시작 위치 지정(offset 지정)
        tuple.offset(pageable.getOffset());

        // 10) tuple의 데이터를 가져올 때 개수 지정
        tuple.limit(pageable.getPageSize());

        // 11) 최종결과를 tuple의 fetch()를 통해서 컬렉션으로 변환
        List<Tuple> result = tuple.fetch();

        // 12) tuple의 검색 결과 개수
        long count = tuple.fetchCount();
        log.info("총 개수 출력" + count);

        // 13) Page 객체를 PageImpl 객체로 변환
        return new PageImpl<Object[]>(result.stream()
                .map(t -> t.toArray()).collect(Collectors.toList()), pageable, count);
    }
}
*/
