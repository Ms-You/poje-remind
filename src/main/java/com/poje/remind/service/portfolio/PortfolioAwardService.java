package com.poje.remind.service.portfolio;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.config.SecurityUtil;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.PortfolioAward;
import com.poje.remind.domain.portfolio.dto.PortfolioAwardDTO;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioAwardRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PortfolioAwardService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioAwardRepository portfolioAwardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PortfolioAwardDTO.PortfolioAwardResp enrollPortfolioAward(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        if(member != portfolio.getWriter()) {
            throw new GlobalException(ErrorCode.MEMBER_NOT_MATCH);
        }

        PortfolioAward portfolioAward = PortfolioAward.builder()
                .supervision("주최를 입력해주세요.")
                .grade("순위를 입력해주세요. (e.g.3등 or 동상)")
                .description("설명을 입력해주세요.")
                .portfolio(portfolio)
                .build();

        portfolioAwardRepository.save(portfolioAward);

        return PortfolioAwardDTO.PortfolioAwardResp.builder()
                .portfolioAward(portfolioAward)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PortfolioAwardDTO.PortfolioAwardResp> getPortfolioAwardList(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        List<PortfolioAward> portfolioAwardList = portfolio.getPortfolioAwardList();

        return portfolioAwardList.stream()
                .map(portfolioAward -> PortfolioAwardDTO.PortfolioAwardResp.builder()
                        .portfolioAward(portfolioAward)
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public PortfolioAwardDTO.PortfolioAwardResp updatePortfolioAward(Long portfolioAwardId, PortfolioAwardDTO.UpdateReq updateReq) {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        PortfolioAward portfolioAward = portfolioAwardRepository.findPortfolioAwardWithWriter(portfolioAwardId, member.getId());

        portfolioAward.update(updateReq.getSupervision(), updateReq.getGrade(), updateReq.getDescription());

        return PortfolioAwardDTO.PortfolioAwardResp.builder()
                .portfolioAward(portfolioAward)
                .build();
    }

    @Transactional
    public void deletePortfolioAward(Long portfolioAwardId) {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        PortfolioAward portfolioAward = portfolioAwardRepository.findPortfolioAwardWithWriter(portfolioAwardId, member.getId());

        portfolioAwardRepository.delete(portfolioAward);
    }
}
