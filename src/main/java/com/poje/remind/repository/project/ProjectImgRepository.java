package com.poje.remind.repository.project;

import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectImg;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProjectImgRepository {

    private final EntityManager em;

    public void save(ProjectImg projectImg) {
        em.persist(projectImg);
    }

    public Optional<ProjectImg> findById(Long projectImgId) {
        return Optional.ofNullable(em.find(ProjectImg.class, projectImgId));
    }

    public List<ProjectImg> findAll() {
        return em.createQuery("select pi from ProjectImg pi")
                .getResultList();
    }

    public List<ProjectImg> findByProject(Project project) {
        return em.createQuery("select distinct pi " +
                        "from ProjectImg pi " +
                        "where pi.project = :project")
                .setParameter("project", project)
                .getResultList();
    }

    public void delete(ProjectImg projectImg) {
        em.remove(projectImg);
    }
}
