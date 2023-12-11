package com.example.jpaquerydsl;

import com.example.jpaquerydsl.domain.Member;
import com.example.jpaquerydsl.domain.Team;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Profile("local") // 해당 함수가 local일 경우에만 실행
@Component
@RequiredArgsConstructor
public class MemberInit {

    private final MemberInitService memberInitService;

    @PostConstruct
    public void init() {
        memberInitService.init();
    }

    @Component
    static class MemberInitService {
        @PersistenceContext
        private EntityManager entityManager;

        @Transactional
        // SpringBoot Tomcat이 실행될 때 자동으로 회원과 팀 개체들이 저장
        public void init() {
            Team teamA = new Team("companyA");
            Team teamB = new Team("companyB");
            entityManager.persist(teamA);
            entityManager.persist(teamB);

            for (int i = 0; i < 50; i++) {
                Team getTeam = i % 2 == 0 ? teamA : teamB;
                entityManager.persist(new Member("yang" + i, i, getTeam));
            }
        }
    }
}
