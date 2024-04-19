package com.poje.remind.service.portfolio;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.common.PagingDTO;
import com.poje.remind.common.PagingUtil;
import com.poje.remind.config.SecurityUtil;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.portfolio.Like;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.dto.PortfolioDTO;
import com.poje.remind.domain.portfolio.dto.PortfolioLikeDTO;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioLikeRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PortfolioLikeService {

    private final PortfolioLikeRepository portfolioLikeRepository;
    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PortfolioLikeDTO.PortfolioLikeResp likePortfolio(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new GlobalException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Optional<Like> likeOptional = portfolioLikeRepository.findByMemberAndPortfolio(member, portfolio);
        boolean likeStatus;

        if(likeOptional.isPresent()) {
            portfolioLikeRepository.delete(likeOptional.get());
            likeStatus = false;
        } else {
            Like like = Like.builder()
                    .member(member)
                    .portfolio(portfolio)
                    .build();

            portfolioLikeRepository.save(like);
            likeStatus = true;
        }

        Long likeCount = portfolioLikeRepository.countByPortfolio(portfolio);

        return new PortfolioLikeDTO.PortfolioLikeResp(likeStatus, likeCount);
    }

    @Transactional(readOnly = true)
    public PortfolioDTO.PortfolioAndMemberListResp getPortfolioWhichLikes(int page) {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        List<Portfolio> portfolioList = portfolioRepository.findPortfolioWhichMemberLike(member);

        PagingDTO pagingDTO = new PagingDTO(page);
        PagingUtil pagingUtil = new PagingUtil(portfolioList.size(), pagingDTO);

        // limit으로 가져올 포트폴리오 목록
        List<Portfolio> pagingPortfolioList = portfolioRepository.findPortfolioWhichMemberLike(member, pagingDTO.limitCalc());

        return PortfolioDTO.PortfolioAndMemberListResp.builder()
                .portfolioList(pagingPortfolioList)
                .pagingUtil(pagingUtil)
                .build();
    }
}
