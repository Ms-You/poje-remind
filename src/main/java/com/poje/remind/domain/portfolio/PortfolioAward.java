package com.poje.remind.domain.portfolio;

import com.poje.remind.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "portfolio_award")
@Entity
public class PortfolioAward extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_award_id")
    private Long id;

    private String supervision;
    private String grade;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Builder
    private PortfolioAward(String supervision, String grade, String description, Portfolio portfolio) {
        this.supervision = supervision;
        this.grade = grade;
        this.description = description;
        this.portfolio = portfolio;

        portfolio.getPortfolioAwardList().add(this);
    }
}
