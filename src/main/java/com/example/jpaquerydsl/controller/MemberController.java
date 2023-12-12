package com.example.jpaquerydsl.controller;

import com.example.jpaquerydsl.domain.MemberSearch;
import com.example.jpaquerydsl.domain.MemberTeamDTO;
import com.example.jpaquerydsl.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public ResponseEntity<List<MemberTeamDTO>> findMemberBySearchKeyword(MemberSearch memberSearch) {
        // Builder를 통한 회원 및 팀조회 시
        // return ResponseEntity.ok(memberService.findMemberBySearchKeyword(userName, teamName));
        return ResponseEntity.ok(memberService.findMemberBySearchKeyword(memberSearch));
    }

    @GetMapping("/api/v2/members")
    public ResponseEntity<Page<MemberTeamDTO>> findMemberBySearchKeywordAndPageSimple(MemberSearch memberSearch, Pageable pageable) {
        return ResponseEntity.ok(memberService.findMemberBySearchKeywordAndPageSimple(memberSearch, pageable));
    }

    @GetMapping("/api/v3/members")
    public ResponseEntity<Page<MemberTeamDTO>> findMemberBySearchKeywordAndPage(MemberSearch memberSearch, Pageable pageable) {
        return ResponseEntity.ok(memberService.findMemberBySearchKeywordAndPage(memberSearch, pageable));
    }
}
