package com.poje.remind.service.portfolio;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.common.PagingDTO;
import com.poje.remind.common.PagingUtil;
import com.poje.remind.config.SecurityUtil;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.dto.PortfolioDTO;
import com.poje.remind.repository.ability.JobRepository;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioLikeRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;
    private final JobRepository jobRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;

    @Transactional
    public PortfolioDTO.BasicPortfolioResp enrollBasicPortfolio(String jobName) {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Job job = jobRepository.findByName(jobName).orElseThrow(
                () -> new GlobalException(ErrorCode.JOB_NOT_FOUND)
        );

        Portfolio portfolio = Portfolio.builder()
                .title("제목을 입력해주세요.")
                .description("내용을 입력해주세요.")
                .writer(member)
                .job(job)
                .backgroundImg("DEFAULT_PORTFOLIO_IMG")
                .build();

        portfolioRepository.save(portfolio);

        return new PortfolioDTO.BasicPortfolioResp(portfolio.getId());
    }

    @Transactional(readOnly = true)
    public PortfolioDTO.PortfolioInfoResp getPortfolio(Long portfolioId) {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        // 현재 사용자가 포트폴리오 좋아요 눌렀는지 여부
        boolean likeStatus = portfolioLikeRepository.existsByMemberAndPortfolio(member, portfolio);

        return PortfolioDTO.PortfolioInfoResp.builder()
                .portfolio(portfolio)
                .likeStatus(likeStatus)
                .build();
    }

    @Transactional(readOnly = true)
    public PortfolioDTO.PortfolioAndMemberListResp getPortfolioList(String jobName, int page, String keyword) {
        Job job = jobRepository.findByName(jobName).orElseThrow(
                () -> new GlobalException(ErrorCode.JOB_NOT_FOUND)
        );

        PagingDTO pagingDTO = new PagingDTO(page);
        PagingUtil pagingUtil = new PagingUtil(job.getPortfolioList().size(), pagingDTO);

        List<Portfolio> pagingPortfolioList = portfolioRepository.findPortfolioWithJobAndKeyword(job, keyword, pagingDTO.limitCalc());

        return PortfolioDTO.PortfolioAndMemberListResp.builder()
                .portfolioList(pagingPortfolioList)
                .pagingUtil(pagingUtil)
                .build();
    }

    @Transactional(readOnly = true)
    public PortfolioDTO.PortfolioAboutMeResp getPortfolioAboutMe(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        return PortfolioDTO.PortfolioAboutMeResp.builder()
                .portfolio(portfolio)
                .build();
    }

    @Transactional(readOnly = true)
    public PortfolioDTO.PortfolioAndMemberListResp getMemberPortfolioList() {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        return PortfolioDTO.PortfolioAndMemberListResp.builder()
                .portfolioList(member.getPortfolioList())
                .build();
    }

    @Transactional
    public PortfolioDTO.PortfolioInfoResp updatePortfolio(Long portfolioId, PortfolioDTO.UpdateReq updateReq) {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        if(portfolio.getWriter() != member) {
            throw new GlobalException(ErrorCode.WRITER_NOT_MATCHED_PORTFOLIO);
        }

        portfolio.update(updateReq.getTitle(), updateReq.getDescription());

        // 현재 사용자가 포트폴리오 좋아요 눌렀는지 여부
        boolean likeStatus = portfolioLikeRepository.existsByMemberAndPortfolio(member, portfolio);

        return PortfolioDTO.PortfolioInfoResp.builder()
                .portfolio(portfolio)
                .likeStatus(likeStatus)
                .build();
    }

    @Transactional
    public void deletePortfolio(Long portfolioId) {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        if(portfolio.getWriter() != member) {
            throw new GlobalException(ErrorCode.WRITER_NOT_MATCHED_PORTFOLIO);
        }

        portfolioRepository.delete(portfolio);
    }
}
