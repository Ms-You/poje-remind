package com.poje.remind.domain.project;

import com.poje.remind.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_skill")
@Entity
public class ProjectSkill extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_skill_id")
    private Long id;

    private String type;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    private ProjectSkill(String type, String name, Project project) {
        this.type = type;
        this.name = name;
        this.project = project;

        project.getProjectSkillList().add(this);
    }
}
