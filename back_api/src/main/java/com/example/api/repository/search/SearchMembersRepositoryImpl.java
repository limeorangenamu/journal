package com.example.api.repository.search;

import com.example.api.entity.Members;
import com.example.api.entity.QMembers;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchMembersRepositoryImpl implements SearchMembersRepository {
    // QuerydslRepositorySupport의 단일 상속 문제로 인해 JPAQueryFactory로 변경
    private final JPAQueryFactory queryFactory; // QueryDSL을 사용하기 위해 객체선언

    @Override
    // 회원 검색 위한 searchPage 재정의
    public Page<Members> searchPage(String type, String keyword, Pageable pageable) {
        // 1) 도메인 확보
        QMembers members = QMembers.members;

        // 2) 검색 조건 객체 생성
        BooleanExpression condition = searchCondition(type, keyword);

        // 3) 조회하기 위한 실제 SQL을 생성 :: 선언만 한것
        JPAQuery<Members> query = queryFactory.selectFrom(members).where(condition);

        // 4) 정렬을 추가
        applySort(query, pageable, members);

        // 5) SQL 실행함.
        List<Members> content = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        // 6) 전체 개수 파악 :: Count Query
        Long total = queryFactory.select(members.count()).from(members)
                .where(condition).fetchOne();

        // 7) Spring Data의 Page 객체를 생성 :: 목록, 현재 페이지, 전체 개수
        return new PageImpl<>(content, pageable,total == null ? 0 : total);
    }

    /* 전체 검색조건 */
    private BooleanExpression searchCondition(String type, String keyword) {
        BooleanExpression condition = midGtZero(); // 기본 조건 :: mid > 0 항상사용
        BooleanExpression keywordCondition = keywordCondition(type, keyword);
        if (keywordCondition != null) condition = condition.and(keywordCondition);

        return condition;
    }

    /* 기본조건 :: 항상 필요 */
    private BooleanExpression midGtZero() {
        return QMembers.members.mid.gt(0L);
    }

    /* 검색조건 조합 */
    private BooleanExpression keywordCondition(String type, String keyword) {
        if (type == null || type.isBlank()) return null;
        if (keyword == null || keyword.isBlank()) return null;
        BooleanExpression expression = null;
        if (type.contains("e")) expression = emailContains(keyword);
        if (type.contains("n")) {
            if (expression == null)
                expression = nicknameContains(keyword);
            else
                expression = expression.or(nicknameContains(keyword));
        }
        return expression;
    }

    /* 이메일 검색 */
    private BooleanExpression emailContains(String keyword) {
        return QMembers.members.email.containsIgnoreCase(keyword);
    }

    /* 닉네임 검색 */
    private BooleanExpression nicknameContains(String keyword) {
        return QMembers.members.nickname.containsIgnoreCase(keyword);
    }

    /* 정렬 적용 */
    private void applySort(JPAQuery<?> query, Pageable pageable, EntityPathBase<?> entity) {
        pageable.getSort().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            query.orderBy(new OrderSpecifier<>(
                            direction,
                            Expressions.path(
                                    Comparable.class,
                                    entity,
                                    order.getProperty()
                            )
                    )
            );
        });
    }
}