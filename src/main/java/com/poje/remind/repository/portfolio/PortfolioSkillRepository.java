package com.poje.remind.repository.portfolio;

import com.poje.remind.domain.portfolio.PortfolioSkill;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class PortfolioSkillRepository {

    private final EntityManager em;

    @Transactional
    public void save(PortfolioSkill portfolioSkill) {
        em.persist(portfolioSkill);
    }

    @Transactional(readOnly = true)
    public Optional<PortfolioSkill> findById(Long portfolioSkillId) {
        return Optional.ofNullable(em.find(PortfolioSkill.class, portfolioSkillId));
    }

    @Transactional
    public void delete(PortfolioSkill portfolioSkill) {
        em.remove(portfolioSkill);
    }

    @Transactional(readOnly = true)
    public List<PortfolioSkill> findAll() {
        return em.createQuery("select ps from PortfolioSkill ps")
                .getResultList();
    }

    @Transactional
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
