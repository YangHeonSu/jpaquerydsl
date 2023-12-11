package com.example.jpaquerydsl;

import com.example.jpaquerydsl.domain.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.jpaquerydsl.domain.QMember.member;
import static com.example.jpaquerydsl.domain.QTeam.team;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
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

    @Test
    void groupByTest() {
        List<Tuple> result = jpaQueryFactory.select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name) // Inner Join으로 인해 중복된 값으로 리스트 개수 만큼 나오는 것을 방지
                .fetch();

        Tuple team1 = result.get(0);
        Tuple team2 = result.get(1);

        assertThat(team1.get(team.name)).isEqualTo("companyA");
        assertThat(team1.get(member.age.avg())).isEqualTo(15);
    }

    @Test
    void joinTest() {
        List<Member> companyA = jpaQueryFactory.selectFrom(member)
                .leftJoin(member.team, team)
                .where(team.name.eq("companyA"))
                .fetch(); // JPQL Join 쿼리와 동일함.

        assertThat(companyA)
                .extracting("name")
                .containsExactly("yang1", "yang2"); // 결과에서 해당 이름들이 포함되었는지 확인
    }

    // member와 team join 후 team 이름이 companyA인 team만 조인, member는 모두 조회
    @Test
    void joinTest1() {
        //연관관계가 있는 조인일 경우
        List<Tuple> companyA1 = jpaQueryFactory.select(member, team)
                .from(member)
                .leftJoin(member.team, team)
//                .where(team.name.eq("companyA")) -> inner join일 경우 on절을 사용했을 때와 where절이 사용했을 시가 동일
                .on(team.name.eq("companyA"))
                .fetch();
        for (Tuple tuple : companyA1) {
            System.out.println("tuple : " + tuple);
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@@");
        entityManager.persist(new Member("companyA"));
        entityManager.persist(new Member("companyB"));


        //연관관계가 없는 세타 조인일 경우
        List<Tuple> companyA2 = jpaQueryFactory.select(member, team)
                .from(member)
                .leftJoin(team)
                .on(team.name.eq(member.name))
                .fetch();

        for (Tuple tuple : companyA2) {
            System.out.println("tuple : " + tuple);
        }
    }

    @Test
    void subQueryTest() {

        /**
         * JPA에서 서브쿼리는 select, where절에서만 가능
         * 서브쿼리 작성시 QMember memberSub = new QMember("memberSub") 처럼 alias 새로 생성
         */
        QMember memberSub = new QMember("memberSub");
        // where절 subQuery
        List<Member> fetch1 = jpaQueryFactory.selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                )).fetch();
        for (Member member1 : fetch1) {
            System.out.println("member = " + member1);
        }

        System.out.println("@@@@@@@@@@@@@@@@@@@@");
        // select절 subQuery
        List<Tuple> fetch = jpaQueryFactory.select(member.name
                        , JPAExpressions.select(memberSub.age.avg()).from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    void projectionsResultJPQL() {
        // 순수 JPA으로 프로잭션 결과를 DTO로 반환할 경우 반환 DTO를 패키지명부터 작성하고 DTO 생성자 필수(AllArgsConstructor)
        List<MemberDTO> members = entityManager.createQuery("select new com.example.jpaquerydsl.domain.MemberDTO(m.name, m.age) from Member m", MemberDTO.class)
                .getResultList();
        for (MemberDTO memberDTO : members) {
            System.out.println("memberDTO = " + memberDTO);
        }
    }

    @Test
    void projectionResultQueryDsl() {

        /*
         * Querydsl에서 프로잭션 결과를 DTO로 반환할 경우 방법은 3가지
         * 1. 프로퍼티 접근 Projections.bean -> DTO의 Setter !!@NoArgsConstructor 필수
         * 2. 필드 직접 접근 Projections.field -> DTO의 필드에 바로 값이 들어감
         * 3. 생성자 접근 Projections.construct -> DTO 필드 타입과 맞아야함 member.name -> dto의 name의 타입과 같아야함
         */
        // 프로퍼티 접근
        List<MemberDTO> member1 = jpaQueryFactory.select(Projections.bean(MemberDTO.class
                        , member.name
                        , member.age))
                .from(member)
                .fetch();
        for (MemberDTO memberDTO : member1) {
            System.out.println("memberDTO = " + memberDTO);
        }
        // 필드 직접 접근
        List<MemberDTO> member2 = jpaQueryFactory.select(Projections.fields(MemberDTO.class
                        , member.name
                        , member.age))
                .from(member)
                .fetch();
        for (MemberDTO memberDTO : member2) {
            System.out.println("memberDTO = " + memberDTO);
        }
        // 생성자 접근
        List<MemberDTO> member3 = jpaQueryFactory.select(Projections.constructor(MemberDTO.class
                        , member.name
                        , member.age))
                .from(member)
                .fetch();
        for (MemberDTO memberDTO : member3) {
            System.out.println("memberDTO = " + memberDTO);
        }
    }

    @Test
    void projectionResultQueryProjection() {
        // Projections.constructor와 비슷하지만 차이점은 @QueryProjection은 컴퍼일시점부터 오류를 찾을 수 있음.
        List<MemberDTO> members = jpaQueryFactory.select(new QMemberDTO(member.name, member.age))
                .from(member)
                .fetch();
        for (MemberDTO memberDTO : members) {
            System.out.println("memberDTO = " + memberDTO);
        }
    }

    @Test
    void dynamicQuery() {
        String name = "yang";
        Integer age = 10;
        List<Member> members = search(name, age);

        for (Member member1 : members) {
            System.out.println("member1 = " + member1);
        }
    }

    List<Member> search(String nameValue, Integer ageValue) {
        // 동적 쿼리 BooleanBuilder 사용
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (nameValue != null) {
            booleanBuilder.and(member.name.contains(nameValue));
        }

        if (ageValue!= null) {
            booleanBuilder.and(member.age.eq(ageValue));
        }
        return jpaQueryFactory.select(member)
                .from(member)
                .where(booleanBuilder)
                .fetch();
    }

    @Test
    void dynamicQuery2() {
        String name = "yang";
        Integer age = 10;
        List<Member> members = findMember(name, age);

        for (Member member1 : members) {
            System.out.println("member1 = " + member1);
        }
    }

    List<Member> findMember(String nameValue, Integer ageValue) {
        // 동적 쿼리 where MultiParameter 사용 -> 재사용 가능하다는 장점
        return jpaQueryFactory.select(member)
                .from(member)
                .where(nameContain(nameValue), ageEq(ageValue))
                .fetch();
    }

    private Predicate nameContain(String nameValue) {
        return nameValue != null ? member.name.contains(nameValue) : null;
    }

    private Predicate ageEq(Integer ageValue) {
        return ageValue != null ? member.age.eq(ageValue) : null;
    }
    
    @Test
    @Commit
    void bulkUpdate() {
        long updateCount = jpaQueryFactory.update(member)
                .set(member.name, "changeYang")
                .where(member.age.lt(28)) // 28살 이하일 경우
                .execute();

        // bulk연산 시 문제점으로 DB는 값이 변경되었지만 영속성 컨텍스트에는 적용이 되지않아 SELECT 시 변경 전 값으로 가져옴
        // 해결방안으로 영속성 컨텍스트에 있는 것을 DB에 보내고 초기화 flush, clear
        entityManager.flush();
        entityManager.clear();

        List<Member> members = jpaQueryFactory.selectFrom(member).fetch();
        for (Member member1 : members) {
            System.out.println("member1 = " + member1);
        }
    }
}