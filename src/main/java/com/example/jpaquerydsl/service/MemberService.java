package com.example.jpaquerydsl.service;

import com.example.jpaquerydsl.domain.MemberSearch;
import com.example.jpaquerydsl.domain.MemberTeamDTO;
import com.example.jpaquerydsl.repository.MemberJpaRepository;
import com.example.jpaquerydsl.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;

    public List<MemberTeamDTO> findMemberByBuilder(MemberSearch memberSearch) {
        return memberJpaRepository.findMemberByBuilder(memberSearch);
    }

    public List<MemberTeamDTO> findMemberBySearchKeyword(MemberSearch memberSearch) {
        return memberJpaRepository.findMemberBySearchKeyword(memberSearch);
    }

    /**
     * 페이징 및 동적쿼리를 적용하여 회원정보 조회
     */
    public Page<MemberTeamDTO> findMemberBySearchKeywordAndPageSimple(MemberSearch memberSearch, Pageable pageable) {
        return memberRepository.findMemberBySearchKeywordAndPageSimple(memberSearch,pageable);
    }

    /**
     * 페이징 최적화 및 동적쿼리를 적용하여 회원정보 조회
     */
    public Page<MemberTeamDTO> findMemberBySearchKeywordAndPage(MemberSearch memberSearch, Pageable pageable) {
        return memberRepository.findMemberBySearchKeywordAndPage(memberSearch,pageable);
    }
}