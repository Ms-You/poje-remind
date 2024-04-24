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

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PortfolioSkillService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioSkillRepository portfolioSkillRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void updatePortfolioSkill(Long portfolioId, PortfolioSkillDTO.UpdateReqList updateReqList) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        if(!portfolio.getWriter().equals(member)) {
            throw new GlobalException(ErrorCode.MEMBER_NOT_MATCH);
        }

        List<PortfolioSkill> currentSkills = portfolio.getPortfolioSkillList();

        // 업데이트 하려는 스킬 목록
        Set<String> newSkillsSet = updateReqList.getUpdateReqList().stream()
                .map(PortfolioSkillDTO.UpdateReq::getName)
                .collect(Collectors.toSet());

        // 기존 스킬 중 업데이트 되지 않는 스킬 삭제
        currentSkills.stream()
                .filter(skill -> !newSkillsSet.contains(skill.getName()))
                .forEach(portfolioSkillRepository::delete);

        for(PortfolioSkillDTO.UpdateReq updateSkill : updateReqList.getUpdateReqList()) {
            // 기존에 같은 이름의 스킬이 있는지 확인
            Optional<PortfolioSkill> existingSkillOpt = currentSkills.stream()
                    .filter(skill -> skill.getName().equals(updateSkill.getName()))
                    .findFirst();

            if(!existingSkillOpt.isPresent()) {
                // 새로운 기술 저장
                PortfolioSkill newSkill = PortfolioSkill.builder()
                        .name(updateSkill.getName())
                        .path(updateSkill.getPath())
                        .portfolio(portfolio)
                        .build();

                portfolioSkillRepository.save(newSkill);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<PortfolioSkillDTO.PortfolioSKillResp> getPortfolioSkill(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        List<PortfolioSkill> skillList = portfolio.getPortfolioSkillList();

        return skillList.stream()
                .map(skill -> new PortfolioSkillDTO.PortfolioSKillResp(skill.getId(), skill.getName(), skill.getPath()))
                .collect(Collectors.toList());
    }

}
