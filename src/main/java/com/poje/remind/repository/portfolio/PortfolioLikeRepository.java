package com.poje.remind.repository.portfolio;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.portfolio.Like;
import com.poje.remind.domain.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioLikeRepository extends JpaRepository<Like, Long> {
    boolean existsByMemberAndPortfolio(Member member, Portfolio portfolio);
    Optional<Like> findByMemberAndPortfolio(Member member, Portfolio portfolio);

    Long countByPortfolio(Portfolio portfolio);
}
