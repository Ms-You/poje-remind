package com.poje.remind.repository.portfolio;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.portfolio.Like;
import com.poje.remind.domain.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioLikeRepository extends JpaRepository<Like, Long> {
    boolean existsByMemberAndPortfolio(Member member, Portfolio portfolio);
}
