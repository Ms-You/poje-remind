package com.poje.remind.domain.portfolio;

import com.poje.remind.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "portfolio_skill")
@Entity
public class PortfolioSkill extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_skill_id")
    private Long id;

    private String type;
    private String name;
    @Column(length = 500)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Builder
    private PortfolioSkill(String type, String name, String path, Portfolio portfolio) {
        this.type = type;
        this.name = name;
        this.path = path;
        this.portfolio = portfolio;

        portfolio.getPortfolioSkillList().add(this);
    }
}
