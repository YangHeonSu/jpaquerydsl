package com.example.jpaquerydsl.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name", "age"}) // 연관관계는 ToString 설정 시 무한루프
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String name;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String name, int age, Team team) {
        this.name = name;
        this.age = age;
        if (team != null) {
            teamChange(team); // 팀 변경
        }
    }

    public Member(String name) {
        this(name, 0);
    }

    public Member(String name, int age) {
        this(name, age, null);
    }

    /**
     * 팀 변경
     *
     * @param team team
     */
    private void teamChange(Team team) {
        this.team = team;
        team.getMembers().add(this); //
    }

}
