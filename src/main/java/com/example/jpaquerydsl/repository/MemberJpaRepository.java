package com.example.jpaquerydsl.repository;

import com.example.jpaquerydsl.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.jpaquerydsl.entity.QMember.member;

// 순수 JPA Repository
@Repository
public class MemberJpaRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    public MemberJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 회원 등록 (순수 JPA)
     */
    public void save(Member member) {
        entityManager.persist(member);
    }

    /**
     * 회원 목록 전제 조회
     */
    public List<Member> findAll() {
        // 순수 JPA
        //return entityManager.createQuery("select m from Member m", Member.class).getResultList();
        // Querydsl
        return jpaQueryFactory.selectFrom(member).fetch();
    }

    /**
     * 회원 이름으로 회원 목록 조회
     */
    public List<Member> findByName(String name) {
        // 순수 JPA
        //return entityManager.createQuery("select m from Member m where m.name = :name", Member.class)
        //        .setParameter("name", name)
        //        .getResultList();
        // Querydsl
        return jpaQueryFactory.selectFrom(member)
                .where(member.name.eq(name))
                .fetch();
    }

    /**
     * 회원 식별자로 회원 조회
     */
    public Optional<Member> findById(Long id) {
        // 순수 JPA
        //return Optional.ofNullable(entityManager.find(Member.class, id));
        // Querydsl
        return Optional.ofNullable(jpaQueryFactory.selectFrom(member).where(member.id.eq(id)).fetchOne());
    }

}
