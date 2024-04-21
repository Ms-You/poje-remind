package com.poje.remind.repository.project;

import com.poje.remind.domain.project.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProjectRepository extends JpaRepository<Project, Long> {
    @EntityGraph(attributePaths = {"projectSkillSet", "projectImgSet", "projectAward"})
    List<Project> findAllByPortfolioId(Long portfolioId);

    @Query(value = "select pr " +
            "from Project pr " +
            "JOIN FETCH pr.portfolio pf " +
            "where pr.id = :projectId " +
            "and pf.writer.id = :memberId")
    public Optional<Project> findByProjectIdAndMemberId(@Param("projectId") Long projectId,
                                                        @Param("memberId") Long memberId);

}
