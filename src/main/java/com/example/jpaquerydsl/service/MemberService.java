package com.example.jpaquerydsl.service;

import com.example.jpaquerydsl.domain.MemberTeamDTO;
import com.example.jpaquerydsl.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;

    public List<MemberTeamDTO> findMemberByBuilder(String userName, String teamName) {
        return memberJpaRepository.findMemberByBuilder(userName, teamName);
    }

    public List<MemberTeamDTO> findMemberBySearchKeyword(String userName, String teamName) {
        return memberJpaRepository.findMemberBySearchKeyword(userName, teamName);
    }
}
