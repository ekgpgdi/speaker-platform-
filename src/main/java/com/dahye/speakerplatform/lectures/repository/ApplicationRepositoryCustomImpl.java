package com.dahye.speakerplatform.lectures.repository;

import com.dahye.speakerplatform.common.enums.SortDirection;
import com.dahye.speakerplatform.lectures.dto.response.ApplicantUserResponse;
import com.dahye.speakerplatform.lectures.dto.response.LectureApplicationResponse;
import com.dahye.speakerplatform.lectures.enums.LectureApplicationSort;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.dahye.speakerplatform.lectures.domain.QApplication.application;
import static com.dahye.speakerplatform.lectures.domain.QLecture.lecture;
import static com.dahye.speakerplatform.users.entity.QUser.user;

public class ApplicationRepositoryCustomImpl implements ApplicationRepositoryCustom {
    private JPAQueryFactory queryFactory;

    public ApplicationRepositoryCustomImpl(EntityManager em) {
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
                default -> ascending ? application.createdAt.asc() : application.createdAt.desc();
            };
        }

        return orderSpecifier;
    }

    @Override
    public Page<LectureApplicationResponse> getLectureApplicationListByLectureStartTime(int page, int size, String employeeNo, LectureApplicationSort sort, SortDirection sortDirection) {
        LocalDateTime oneDayAge = LocalDateTime.now().minusDays(1);
        LocalDateTime oneWeekAhead = LocalDateTime.now().plusWeeks(1);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection.getDirection(), sort.getFieldName()));

        List<LectureApplicationResponse> lectureApplicationResponseList = queryFactory
                .select(Projections.constructor(LectureApplicationResponse.class,
                        lecture.id,
                        lecture.lecturer,
                        lecture.location,
                        lecture.capacity,
                        lecture.currentCapacity,
                        lecture.startTime,
                        lecture.content,
                        application.id))
                .from(application)
                .join(lecture).on(application.lecture.id.eq(lecture.id))
                .join(application.user)
                .where(
                        user.employeeNo.eq(employeeNo),
                        lecture.startTime.between(oneDayAge, oneWeekAhead)
                )
                .orderBy(getOrderSpecifierBySort(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(application.count())
                .from(application)
                .join(lecture).on(application.lecture.id.eq(lecture.id))
                .join(application.user)
                .where(
                        user.employeeNo.eq(employeeNo),
                        lecture.startTime.between(oneDayAge, oneWeekAhead)
                )
                .fetchOne();

        return new PageImpl<>(lectureApplicationResponseList, pageable, total);

    }

    @Override
    public Page<ApplicantUserResponse> getLectureApplicantUserList(Long lectureId, Pageable pageable) {

        List<ApplicantUserResponse> applicantUserList = queryFactory
                .select(Projections.constructor(ApplicantUserResponse.class,
                        user.employeeNo,
                        application.createdAt))
                .from(application)
                .innerJoin(application.lecture, lecture)
                .innerJoin(application.user, user)
                .where(lecture.id.eq(lectureId))
                .orderBy(application.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(application.count())
                .from(application)
                .innerJoin(application.lecture, lecture)
                .innerJoin(application.user, user)
                .where(lecture.id.eq(lectureId))
                .fetchOne();

        return new PageImpl<>(applicantUserList, pageable, total);
    }
}
