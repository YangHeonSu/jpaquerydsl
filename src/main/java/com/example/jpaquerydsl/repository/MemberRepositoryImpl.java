package com.example.jpaquerydsl.repository;

import com.example.jpaquerydsl.domain.MemberSearch;
import com.example.jpaquerydsl.domain.MemberTeamDTO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.jpaquerydsl.domain.QMember.member;
import static com.example.jpaquerydsl.domain.QTeam.team;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public MemberRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
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

    @Override
    public Page<MemberTeamDTO> findMemberBySearchKeywordAndPageSimple(MemberSearch memberSearch, Pageable pageable) {
        QueryResults<MemberTeamDTO> memberTeamDTOQueryResults = jpaQueryFactory
                .select(Projections.fields(MemberTeamDTO.class,
                        member.id.as("memberId"),
                        member.name.as("userName"),
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .offset(pageable.getOffset()) // 0부터 시작
                .limit(pageable.getPageSize())
                .where(userNameContain(memberSearch.getUserName()), teamNameContain(memberSearch.getTeamName()))
                .fetchResults();

        List<MemberTeamDTO> content = memberTeamDTOQueryResults.getResults();
        long total = memberTeamDTOQueryResults.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDTO> findMemberBySearchKeywordAndPage(MemberSearch memberSearch, Pageable pageable) {
        List<MemberTeamDTO> content = jpaQueryFactory
                .select(Projections.fields(MemberTeamDTO.class,
                        member.id.as("memberId"),
                        member.name.as("userName"),
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .offset(pageable.getOffset()) // 0부터 시작
                .limit(pageable.getPageSize())
                .where(userNameContain(memberSearch.getUserName()), teamNameContain(memberSearch.getTeamName()))
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory
                .select(member.count())
                .from(member)
                .leftJoin(member.team, team)
                .where(userNameContain(memberSearch.getUserName()), teamNameContain(memberSearch.getTeamName()));

        // countQuery 최적화 적용
        return PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchOne);
    }

    private BooleanExpression userNameContain(String userName) {
        return StringUtils.hasText(userName) ? member.name.contains(userName) : null;
    }

    private BooleanExpression teamNameContain(String teamName) {
        return StringUtils.hasText(teamName) ? team.name.contains(teamName) : null;
    }
}
