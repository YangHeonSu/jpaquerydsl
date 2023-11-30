package com.example.jpaquerydsl;

import com.example.jpaquerydsl.entity.Member;
import com.example.jpaquerydsl.entity.QMember;
import com.example.jpaquerydsl.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.jpaquerydsl.entity.QMember.member;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
class QuerydslTest {

    @Autowired
    EntityManager entityManager;
    JPAQueryFactory jpaQueryFactory;

    @BeforeEach  // 테스트 케이스가 실행되기 전에 먼저 실행되는 설정
    public void setUp() {
        jpaQueryFactory = new JPAQueryFactory(entityManager);
        Team teamA = new Team("companyA");
        Team teamB = new Team("companyB");

        entityManager.persist(teamA);
        entityManager.persist(teamB);

        Member member1 = new Member("yang1", 10, teamA);
        Member member2 = new Member("yang2", 20, teamA);
        Member member3 = new Member("yang3", 30, teamB);
        Member member4 = new Member("yang4", 40, teamB);
        Member member5 = new Member("yang5", 50, teamB);

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.persist(member3);
        entityManager.persist(member4);
        entityManager.persist(member5);
    }

    @Test
    void jpqlTest() {
        Member member = entityManager.createQuery("select m from Member m where m.name =:name", Member.class)
                .setParameter("name", "yang1")
                .getSingleResult();
        assertThat(member.getName().equals("yang1"));
    }

    @Test
    void querydslTest() {
        Member memberInfo = jpaQueryFactory
                .select(member)
                .from(member)
                .where(member.name.eq("yang1"))
                .fetchOne();

        assertThat(memberInfo.getName().equals("yang1"));
    }

    @Test
    void searchTest() {
        Member memberInfo = jpaQueryFactory.selectFrom(member)
                .where(member.name.eq("yang1"), member.age.eq(10))
                .fetchOne();

        assertThat(memberInfo.getName()).isEqualTo("yang1");
    }

    @Test
    void getFetchResult() {
        /*
         * fetch -> 리스트 조회, 데이터 없으면 빈 값 반환
         * fetchOne -> 단 건 조회 (findById)
         *          -> 결과가 없으면 null
         *          -> 결과가 두개 이상이면 Exception
         * fetchFirst -> limit(1), fetchOne()
         * fetchResult -> 페이징 정보 포홤. total count 쿼리 추가 실행됨
         * fetchCount -> 결과 개수
         */
        List<Member> member1 = jpaQueryFactory.selectFrom(member).fetch();
        Member member2 = jpaQueryFactory.selectFrom(member).fetchOne();
        Member member3 = jpaQueryFactory.selectFrom(QMember.member).fetchFirst();
        QueryResults<Member> member4 = jpaQueryFactory.selectFrom(member).fetchResults();
    }

    /**
     * 회원 정렬 테스트 케이스
     * 나이 내림차순(desc), 이름 오름차순(asc), 이름이 없으면 마지막 출력(nulls last)
     */
    @Test
    void sortMember() {
        entityManager.persist(new Member("yang6", 12));
        entityManager.persist(new Member(null, 122));

        List<Member> members = jpaQueryFactory.selectFrom(member)
                .orderBy(member.age.desc(), member.name.asc().nullsLast()) //nullsLast() -> null이면 마지막에 출력
                .fetch();

        for (Member member : members) {
            System.out.println(member.getName() + "//" + member.getAge());
        }
    }

    @Test
    void pagingTest() {
        // List만 가져옴
        List<Member> member = jpaQueryFactory.selectFrom(QMember.member)
                .orderBy(QMember.member.name.desc().nullsLast())
                .offset(0) // 몇 번째부터 가져올 것인지 0 -> 1개부터 가져옴 1 -> 2개부터
                .limit(3) // 몇개 가져올 것인지
                .fetch();

        // List와 totalCount 같이 가져옴 단순한 페이징 처리 용이 , 복잡한 페이징 시에는 따로 작성해야할 수도 있음
        // member1.getTotal() -> 가져온 목록 개수가 아닌 전체 개수
        QueryResults<Member> member1 = jpaQueryFactory
                .selectFrom(QMember.member)
                .orderBy(QMember.member.name.desc().nullsLast())
                .offset(0).limit(3)
                .fetchResults();
    }
}