package com.poje.remind.domain.portfolio;

import com.poje.remind.common.BaseEntity;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.project.Project;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "portfolio")
@Entity
public class Portfolio extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long id;

    private String title;
    private String description;
    private String backgroundImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @OneToMany(mappedBy = "portfolio", orphanRemoval = true)
    private List<PortfolioAward> portfolioAwardList = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", orphanRemoval = true)
    private List<PortfolioSkill> portfolioSkillList = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", orphanRemoval = true)
    private List<Project> projectList = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", orphanRemoval = true)
    private List<Like> likeList = new ArrayList<>();

    @Builder
    private Portfolio(Long id, String title, String description, String backgroundImg, Member writer, Job job) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.backgroundImg = backgroundImg;
        this.writer = writer;
        this.job = job;

        writer.getPortfolioList().add(this);
        job.getPortfolioList().add(this);
    }

    public void update(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
