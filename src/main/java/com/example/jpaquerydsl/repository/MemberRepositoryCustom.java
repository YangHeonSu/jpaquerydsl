package com.example.jpaquerydsl.repository;

import com.example.jpaquerydsl.domain.MemberSearch;
import com.example.jpaquerydsl.domain.MemberTeamDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    /**
     *  동적쿼리 where절을 통해 회원 및 팀 정보 조회
     */
    List<MemberTeamDTO> findMemberBySearchKeyword(MemberSearch memberSearch);

    Page<MemberTeamDTO> findMemberBySearchKeywordAndPageSimple(MemberSearch memberSearch, Pageable pageable);

    Page<MemberTeamDTO> findMemberBySearchKeywordAndPage(MemberSearch memberSearch, Pageable pageable);

}
