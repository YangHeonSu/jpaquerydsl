package com.example.jpaquerydsl.controller;

import com.example.jpaquerydsl.domain.MemberTeamDTO;
import com.example.jpaquerydsl.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/api/members")
    public ResponseEntity<List<MemberTeamDTO>> findMemberBySearchKeyword(String userName, String teamName) {
        // Builder를 통한 회원 및 팀조회 시
        // return ResponseEntity.ok(memberService.findMemberBySearchKeyword(userName, teamName));
        return ResponseEntity.ok(memberService.findMemberBySearchKeyword(userName, teamName));
    }
}
