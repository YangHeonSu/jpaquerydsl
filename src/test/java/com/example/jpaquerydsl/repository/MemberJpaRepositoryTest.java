package com.example.jpaquerydsl.repository;

import com.example.jpaquerydsl.entity.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void querydslTest() {
        Member member = new Member("yang1", 28);
        memberJpaRepository.save(member);

        List<Member> members = memberJpaRepository.findAll();
        assertThat(members).containsExactly(member);

        Member memberById = memberJpaRepository.findById(member.getId()).get();
        assertThat(memberById).isEqualTo(member);

        List<Member> membersByName = memberJpaRepository.findByName("yang1");
        assertThat(membersByName).containsExactly(member);
    }

}