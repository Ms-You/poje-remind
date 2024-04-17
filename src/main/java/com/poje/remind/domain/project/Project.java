package com.poje.remind.domain.project;

import com.poje.remind.common.BaseEntity;
import com.poje.remind.domain.portfolio.Portfolio;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project")
@Entity
public class Project extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String name;
    private String duration;
    private String description;
    private String belong;
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @OneToMany(mappedBy = "project", orphanRemoval = true)
    private List<ProjectImg> projectImgList = new ArrayList<>();

    @OneToMany(mappedBy = "project", orphanRemoval = true)
    private List<ProjectSkill> projectSkillList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_award_id")
    private ProjectAward projectAward;

    @Builder
    private Project(String name, String duration, String description,
                    String belong, String link, Portfolio portfolio) {
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.belong = belong;
        this.link = link;
        this.portfolio = portfolio;

        portfolio.getProjectList().add(this);
    }

    public void addProjectAward(ProjectAward projectAward) {
        this.projectAward = projectAward;
    }
}
