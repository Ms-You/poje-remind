package com.poje.remind.repository.portfolio;

import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.PortfolioSkill;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class PortfolioSkillRepository {

    private final EntityManager em;

    public void save(PortfolioSkill portfolioSkill) {
        em.persist(portfolioSkill);
    }

    public Optional<PortfolioSkill> findById(Long portfolioSkillId) {
        return Optional.ofNullable(em.find(PortfolioSkill.class, portfolioSkillId));
    }

    public void delete(PortfolioSkill portfolioSkill) {
        em.remove(portfolioSkill);
    }

    public List<PortfolioSkill> findAll() {
        return em.createQuery("select ps from PortfolioSkill ps")
                .getResultList();
    }

    public List<String> findDistinctTypeByPortfolio(Portfolio portfolio) {
        return em.createQuery("select distinct ps.type " +
                        "from portfolioSkill ps " +
                        "where ps.portfolio = :portfolio")
                .setParameter("portfolio", portfolio)
                .getResultList();
    }

    public List<PortfolioSkill> findByPortfolioAndType(Portfolio portfolio, String type) {
        return em.createQuery("select ps " +
                        "from PortfolioSkill ps " +
                        "where ps.portfolio = :portfolio " +
                        "and ps.type = :type")
                .setParameter("portfolio", portfolio)
                .setParameter("type", type)
                .getResultList();
    }

    public void deleteAll(List<PortfolioSkill> portfolioSkillList) {
        if(portfolioSkillList == null || portfolioSkillList.isEmpty()) {
            return;
        }

        List<Long> idList = portfolioSkillList.stream()
                .map(PortfolioSkill::getId)
                .collect(Collectors.toList());

        em.createQuery("delete " +
                        "from PortfolioSkill ps " +
                        "where ps.id in :idList")
                .setParameter("idList", idList)
                .executeUpdate();
    }

}
