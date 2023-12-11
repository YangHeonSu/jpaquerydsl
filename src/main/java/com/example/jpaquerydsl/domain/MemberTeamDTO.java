package com.example.jpaquerydsl.domain;

import lombok.Data;

@Data
public class MemberTeamDTO {

    private Long memberId;
    private String userName;
    private int age;
    private Long teamId;
    private String teamName;
}
