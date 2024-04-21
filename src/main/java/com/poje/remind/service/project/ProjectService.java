package com.poje.remind.service.project;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.config.SecurityUtil;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.dto.ProjectDTO;
import com.poje.remind.domain.project.dto.ProjectDefaults;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import com.poje.remind.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final PortfolioRepository portfolioRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void enrollBasicProject(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        if(!portfolio.getWriter().equals(member)) {
            throw new GlobalException(ErrorCode.MEMBER_NOT_MATCH);
        }

        Project project = createDefaultProject(portfolio);
        projectRepository.save(project);
    }

    private Project createDefaultProject(Portfolio portfolio) {
        return Project.builder()
                .name(ProjectDefaults.NAME)
                .duration(ProjectDefaults.DURATION)
                .description(ProjectDefaults.DESCRIPTION)
                .belong(ProjectDefaults.BELONG)
                .link(ProjectDefaults.LINK)
                .portfolio(portfolio)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO.ProjectResp> getProjectList(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        List<Project> projectList = projectRepository.findAllByPortfolioId(portfolio.getId());

        return projectList.stream()
                .map(ProjectDTO.ProjectResp::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProject(Long portfolioId, Long projectId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        if(!portfolio.getWriter().equals(member)) {
            throw new GlobalException(ErrorCode.MEMBER_NOT_MATCH);
        }

        Project project = projectRepository.findByProjectIdAndMemberId(projectId, member.getId()).orElseThrow(
                () -> new GlobalException(ErrorCode.PROJECT_NOT_FOUND)
        );

        projectRepository.delete(project);
    }

}
