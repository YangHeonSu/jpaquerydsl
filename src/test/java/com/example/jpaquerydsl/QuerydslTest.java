package com.example.jpaquerydsl;

import com.example.jpaquerydsl.entity.Member;
import com.example.jpaquerydsl.entity.QMember;
import com.example.jpaquerydsl.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
class QuerydslTest {

    @Autowired
    EntityManager entityManager;

    private final QMember member = QMember.member;

    // 테스트 케이스가 실행되기 전에 먼저 실행되는 설정
    @BeforeEach
    public void setUp() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        entityManager.persist(teamA);
        entityManager.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        Member member3 = new Member("member3", 10, teamB);
        Member member4 = new Member("member4", 10, teamB);
        Member member5 = new Member("member5", 10, teamB);

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.persist(member3);
        entityManager.persist(member4);
        entityManager.persist(member5);
    }


    @Test
    void jpqlTest() {
        Member member = entityManager.createQuery("select m from Member m where m.name =:name", Member.class)
                .setParameter("name", "member1")
                .getSingleResult();
        assertThat(member.getName().equals("member1"));
    }

    @Test
    void querydslTest() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Member memberInfo = jpaQueryFactory
                .select(member)
                .from(member)
                .where(member.name.eq("member1"))
                .fetchOne();

        assertThat(memberInfo.getName().equals("member1"));

    }
}