package com.poje.remind.repository.portfolio;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PortfolioRepository {
    private final EntityManager em;

    @Transactional
    public void save(Portfolio portfolio) {
        em.persist(portfolio);
    }

    @Transactional(readOnly = true)
    public Optional<Portfolio> findById(Long id) {
        return Optional.ofNullable(em.find(Portfolio.class, id));
    }

    @Transactional
    public void delete(Portfolio portfolio) {
        em.remove(portfolio);
    }

    @Transactional(readOnly = true)
    public List<Portfolio> findAll() {
        return em.createQuery("select p from Portfolio p")
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Portfolio> findPortfolioWithJobAndKeyword(Job job, String keyword, int limit) {
        return em.createQuery("select distinct p " +
                        "from Portfolio p " +
                        "where p.job = :job " +
                        "and p.title like CONCAT('%', :keyword, '%')", Portfolio.class)
                .setParameter("job", job)
                .setParameter("keyword", keyword)
                .setFirstResult(limit)
                .setMaxResults(12)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Portfolio> findPortfolioWhichMemberLike(Member member) {
        return em.createQuery("select distinct p " +
                        "from Portfolio p " +
                        "inner join p.likeList l " +
                        "where l.member = :member ", Portfolio.class)
                .setParameter("member", member)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Portfolio> findPortfolioWhichMemberLike(Member member, int limit) {
        return em.createQuery("select distinct p " +
                        "from Portfolio p " +
                        "inner join p.likeList l " +
                        "where l.member = :member", Portfolio.class)
                .setParameter("member", member)
                .setFirstResult(limit)
                .setMaxResults(12)
                .getResultList();
    }
}
