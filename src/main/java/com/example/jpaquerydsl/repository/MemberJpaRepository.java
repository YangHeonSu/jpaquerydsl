package com.example.jpaquerydsl.repository;

import com.example.jpaquerydsl.domain.Member;
import com.example.jpaquerydsl.domain.MemberSearch;
import com.example.jpaquerydsl.domain.MemberTeamDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.example.jpaquerydsl.domain.QMember.member;
import static com.example.jpaquerydsl.domain.QTeam.team;

// 순수 JPA Repository
@Repository
public class MemberJpaRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    public MemberJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 회원 등록 (순수 JPA)
     */
    public void save(Member member) {
        entityManager.persist(member);
    }

    /**
     * 회원 목록 전제 조회
     */
    public List<Member> findAll() {
        // 순수 JPA
        //return entityManager.createQuery("select m from Member m", Member.class).getResultList();
        // Querydsl
        return jpaQueryFactory.selectFrom(member).fetch();
    }

    /**
     * 회원 이름으로 회원 목록 조회
     */
    public List<Member> findByName(String name) {
        // 순수 JPA
        //return entityManager.createQuery("select m from Member m where m.name = :name", Member.class)
        //        .setParameter("name", name)
        //        .getResultList();
        // Querydsl
        return jpaQueryFactory.selectFrom(member)
                .where(member.name.eq(name))
                .fetch();
    }

    /**
     * 회원 식별자로 회원 조회
     */
    public Optional<Member> findById(Long id) {
        // 순수 JPA
        //return Optional.ofNullable(entityManager.find(Member.class, id));
        // Querydsl
        return Optional.ofNullable(jpaQueryFactory.selectFrom(member).where(member.id.eq(id)).fetchOne());
    }
    
    /**
     *  동적쿼리 Builder를 통해 회원 및 팀 정보 조회
     */
    public List<MemberTeamDTO> findMemberByBuilder(MemberSearch memberSearch) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (StringUtils.hasText(memberSearch.getUserName())) {
            booleanBuilder.and(member.name.contains(memberSearch.getUserName()));
        }

        if (StringUtils.hasText(memberSearch.getTeamName())) {
            booleanBuilder.and(team.name.contains(memberSearch.getTeamName()));
        }

       return jpaQueryFactory
                .select(Projections.fields(MemberTeamDTO.class,
                        member.id.as("memberId"),
                        member.name.as("userName"),
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(booleanBuilder)
                .fetch();
    }

    /**
     *  동적쿼리 where절을 통해 회원 및 팀 정보 조회
     */
    public List<MemberTeamDTO> findMemberBySearchKeyword(MemberSearch memberSearch) {
        return jpaQueryFactory
                .select(Projections.fields(MemberTeamDTO.class,
                        member.id.as("memberId"),
                        member.name.as("userName"),
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(userNameContain(memberSearch.getUserName()), teamNameContain(memberSearch.getTeamName()))
                .fetch();
    }

    private BooleanExpression userNameContain(String userName) {
        return StringUtils.hasText(userName) ? member.name.contains(userName) : null;
    }

    private BooleanExpression teamNameContain(String teamName) {
        return StringUtils.hasText(teamName) ? team.name.contains(teamName) : null;
    }


}
