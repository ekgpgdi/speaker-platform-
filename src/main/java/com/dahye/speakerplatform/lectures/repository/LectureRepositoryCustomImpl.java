package com.dahye.speakerplatform.lectures.repository;

import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static com.dahye.speakerplatform.lectures.domain.QApplication.application;
import static com.dahye.speakerplatform.lectures.domain.QLecture.lecture;

public class LectureRepositoryCustomImpl implements LectureRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public LectureRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public OrderSpecifier<?> getOrderSpecifierBySort(Sort sort) {
        OrderSpecifier<?> orderSpecifier = null;

        if (sort != null && sort.iterator().hasNext()) {
            // 첫 번째 정렬 기준 추출
            Sort.Order order = sort.iterator().next();
            String property = order.getProperty();
            boolean ascending = order.isAscending();

            orderSpecifier = switch (property) {
                case "startTime" -> ascending ? lecture.startTime.asc() : lecture.startTime.desc();
                case "capacity" -> ascending ? lecture.capacity.asc() : lecture.capacity.desc();
                case "currentCapacity" -> ascending ? lecture.currentCapacity.asc() : lecture.currentCapacity.desc();
                default -> ascending ? lecture.createdAt.asc() : lecture.createdAt.desc();
            };
        }

        return orderSpecifier;
    }

    @Override
    public Page<Lecture> findByStartTimePlusOneDayGreaterThanEqual(LocalDateTime now, Pageable pageable) {
        // 강연 시작 시간 >= 현재 시간 -1 일
        BooleanExpression condition = lecture.startTime.goe(now.minusDays(1));

        List<Lecture> lectures = queryFactory
                .selectFrom(lecture)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifierBySort(pageable.getSort()))
                .fetch();

        long total = queryFactory
                .select(lecture.count())
                .from(lecture)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(lectures, pageable, total);
    }

    @Override
    public Page<Lecture> findPopularLectures(int periodDays, Pageable pageable) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(periodDays);

        JPQLQuery<Long> applicationCountQuery = JPAExpressions
                .select(application.id.count())
                .from(application)
                .where(application.lecture.id.eq(lecture.id)
                        .and(application.createdAt.goe(startDate)));

        OrderSpecifier<Long> orderByApplicationCount = new OrderSpecifier<>(Order.DESC, applicationCountQuery);

        List<Lecture> lectures = queryFactory
                .selectFrom(lecture)
                .orderBy(orderByApplicationCount)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(lecture.count())
                .from(lecture)
                .fetchOne();

        return new PageImpl<>(lectures, pageable, total);
    }
}
