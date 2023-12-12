package com.example.jpaquerydsl.repository;

import com.example.jpaquerydsl.domain.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {
    @Autowired
    EntityManager entityManager;

    @Autowired MemberRepository memberRepository;

    @Test
    public void querydslTest() {
        Member member = new Member("yang1", 28);
        memberRepository.save(member);

        List<Member> members = memberRepository.findAll();
        assertThat(members).containsExactly(member);

        Member memberById = memberRepository.findById(member.getId()).get();
        assertThat(memberById).isEqualTo(member);

        List<Member> membersByName = memberRepository.findByName("yang1");
        assertThat(membersByName).containsExactly(member);
    }
}
