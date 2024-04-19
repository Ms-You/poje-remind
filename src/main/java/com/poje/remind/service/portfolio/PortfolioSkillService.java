package com.poje.remind.service.portfolio;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.config.SecurityUtil;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.PortfolioSkill;
import com.poje.remind.domain.portfolio.dto.PortfolioSkillDTO;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import com.poje.remind.repository.portfolio.PortfolioSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PortfolioSkillService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioSkillRepository portfolioSkillRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void enrollPortfolioSkill(Long portfolioId, PortfolioSkillDTO.CreateReq createReq) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        if(portfolio.getWriter() != member) {
            throw new GlobalException(ErrorCode.MEMBER_NOT_MATCH);
        }

        for(PortfolioSkillDTO.PortfolioSkillListReq skillList : createReq.getPortfolioSkillListReqList()) {
            for(PortfolioSkillDTO.PortfolioSkillReq skillReq : skillList.getSkillList()) {
                PortfolioSkill portfolioSkill = PortfolioSkill.builder()
                        .type(skillList.getType())
                        .name(skillReq.getName())
                        .path(skillReq.getPath())
                        .portfolio(portfolio)
                        .build();

                portfolioSkillRepository.save(portfolioSkill);
            }
        }
    }

    @Transactional
    public void updatePortfolioSkill(Long portfolioId, PortfolioSkillDTO.UpdateReq updateReq) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        if(portfolio.getWriter() != member) {
            throw new GlobalException(ErrorCode.MEMBER_NOT_MATCH);
        }

        List<PortfolioSkill> currentSkills = portfolio.getPortfolioSkillList();
        Map<String, PortfolioSkill> currentSkillMap = currentSkills.stream()
                .collect(Collectors.toMap(PortfolioSkill::getName, Function.identity()));

        Set<String> newSkillsSet = updateReq.getPortfolioSkillListReqList().stream()
                .flatMap(skillSet -> skillSet.getSkillList().stream())
                .map(PortfolioSkillDTO.PortfolioSkillReq::getName)
                .collect(Collectors.toSet());

        // 삭제할 기술
        List<PortfolioSkill> skillsToDelete = currentSkills.stream()
                .filter(skill -> !newSkillsSet.contains(skill.getName()))
                .collect(Collectors.toList());

        // 삭제할 기술 제거
        portfolioSkillRepository.deleteAll(skillsToDelete);
        currentSkills.removeAll(skillsToDelete);

        // 새로운 기술 추가
        for(PortfolioSkillDTO.PortfolioSkillListReq skillSet : updateReq.getPortfolioSkillListReqList()) {
            for(PortfolioSkillDTO.PortfolioSkillReq skill : skillSet.getSkillList()) {
                if(!currentSkillMap.containsKey(skill.getName())) {
                    PortfolioSkill newSkill = PortfolioSkill.builder()
                            .type(skillSet.getType())
                            .name(skill.getName())
                            .path(skill.getPath())
                            .portfolio(portfolio)
                            .build();

                    portfolioSkillRepository.save(newSkill);
                }
            }
        }
    }


    @Transactional(readOnly = true)
    public List<PortfolioSkillDTO.PortfolioSkillListResp> getPortfolioSkill(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        List<PortfolioSkillDTO.PortfolioSkillListResp> portfolioSkillListRespList = new ArrayList<>();

        // 포트폴리오에서 사용하는 기술들의 type 목록을 가져옴
        List<String> typeList = portfolioSkillRepository.findDistinctTypeByPortfolio(portfolio);

        for(String type : typeList) {
            List<PortfolioSkillDTO.PortfolioSKillResp> portfolioSKillRespList = new ArrayList<>();
            List<PortfolioSkill> skillList = portfolioSkillRepository.findByPortfolioAndType(portfolio, type);

            for(PortfolioSkill skill : skillList) {
                portfolioSKillRespList.add(new PortfolioSkillDTO.PortfolioSKillResp(skill.getId(), skill.getName(), skill.getPath()));
            }

            portfolioSkillListRespList.add(new PortfolioSkillDTO.PortfolioSkillListResp(type, portfolioSKillRespList));
        }

        return portfolioSkillListRespList;
    }

}
