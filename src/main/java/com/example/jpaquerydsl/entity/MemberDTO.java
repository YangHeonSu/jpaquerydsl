package com.example.jpaquerydsl.entity;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDTO {
    private String name;
    private int age;

    @QueryProjection
    public MemberDTO(String name, int age) {
        this.name = name;
        this.age = age;
    }


}
