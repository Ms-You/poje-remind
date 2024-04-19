package com.poje.remind.controller.portfolio;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.domain.portfolio.dto.PortfolioDTO;
import com.poje.remind.domain.portfolio.dto.PortfolioLikeDTO;
import com.poje.remind.service.portfolio.PortfolioLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/member")
@RestController
public class PortfolioLikeController {

    private final PortfolioLikeService portfolioLikeService;

    /**
     * 포트폴리오 좋아요 클릭
     * @param portfolioId
     * @return
     */
    @PostMapping("/portfolio/{portfolio_id}/like")
    public ResponseEntity<BasicResponse> likePortfolio(@PathVariable("portfolio_id") Long portfolioId) {
        PortfolioLikeDTO.PortfolioLikeResp PortfolioLikeResp = portfolioLikeService.likePortfolio(portfolioId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.CREATED.value(), "'좋아요'를 누르셨습니다.", PortfolioLikeResp));
    }

    /**
     * 좋아요 누른 포트폴리오 목록 조회
     * @param page
     * @return
     */
    @GetMapping("/like/portfolio")
    public ResponseEntity<BasicResponse> getPortfolioWhichLikes(@RequestParam(value = "page", required = false) Integer page) {
        if(page == null || page < 1) {
            page = 1;
        }

        PortfolioDTO.PortfolioAndMemberListResp portfolioAndMemberListResp = portfolioLikeService.getPortfolioWhichLikes(page);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "좋아요 누른 포트폴리오 목록 정보 조회", portfolioAndMemberListResp));
    }

}
