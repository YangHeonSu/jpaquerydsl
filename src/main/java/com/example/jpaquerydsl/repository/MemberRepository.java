package com.example.jpaquerydsl.repository;

import com.example.jpaquerydsl.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom { // Spring Data JPA 설정

    /**
     * 회원 이름을 통해 회원 조회 
     */
    List<Member> findByName(String name);
}
