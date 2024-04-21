package com.poje.remind.domain.project;

import com.poje.remind.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_award")
@Entity
public class ProjectAward extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_award_id")
    private Long id;

    private String supervision;
    private String grade;
    private String description;

    @Builder
    private ProjectAward(String supervision, String grade, String description, Project project) {
        this.supervision = supervision;
        this.grade = grade;
        this.description = description;

        project.addProjectAward(this);
    }

    public void update(String supervision, String grade, String description) {
        this.supervision = supervision;
        this.grade = grade;
        this.description = description;
    }
}
