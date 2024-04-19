package com.poje.remind.repository.portfolio;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.domain.portfolio.PortfolioAward;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PortfolioAwardRepository {
    private final EntityManager em;

    public void save(PortfolioAward portfolioAward) {
        em.persist(portfolioAward);
    }

    public Optional<PortfolioAward> findById(Long portfolioAwardId) {
        return Optional.ofNullable(em.find(PortfolioAward.class, portfolioAwardId));
    }

    public void delete(PortfolioAward portfolioAward) {
        em.remove(portfolioAward);
    }

    public List<PortfolioAward> findAll() {
        return em.createQuery("select pa from PortfolioAward pa")
                .getResultList();
    }

    public PortfolioAward findPortfolioAwardWithWriter(Long portfolioAwardId, Long memberId) {
        return em.createQuery("select pa" +
                        "from PortfolioAward pa" +
                        "JOIN FETCH pa.portfolio p" +
                        "where pa.id = :portfolioAwardId" +
                        "and p.writer.id = :memberId", PortfolioAward.class)
                .setParameter("portfolioAwardId", portfolioAwardId)
                .setParameter("memberId", memberId)
                .getResultList()
                .stream()
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.PORTFOLIO_AWARD_NOT_FOUND));
    }



}
