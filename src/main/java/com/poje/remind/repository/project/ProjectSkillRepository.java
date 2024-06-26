package com.poje.remind.repository.project;

import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectSkill;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProjectSkillRepository {

    private final EntityManager em;

    public void save(ProjectSkill projectSkill) {
        em.persist(projectSkill);
    }

    public Optional<ProjectSkill> findById(Long projectSkillId) {
        return Optional.ofNullable(em.find(ProjectSkill.class, projectSkillId));
    }

    public List<ProjectSkill> findAll() {
        return em.createQuery("select ps from ProjectSkill ps")
                .getResultList();
    }

    public void delete(ProjectSkill projectSkill) {
        em.remove(projectSkill);
    }

    public List<ProjectSkill> findByProject(Project project) {
        return em.createQuery("select distinct ps " +
                        "from ProjectSkill ps " +
                        "where ps.project = :project")
                .setParameter("project", project)
                .getResultList();
    }

}
