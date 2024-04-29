package com.poje.remind.controller.portfolio;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.domain.portfolio.dto.PortfolioDTO;
import com.poje.remind.service.portfolio.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;

    /**
     * 기본 포트폴리오 생성
     * @param jobName
     * @return
     */
    @PostMapping("/member/portfolio")
    public ResponseEntity<BasicResponse> createBasicPortfolio(@RequestParam(value = "job") String jobName) {
        PortfolioDTO.BasicPortfolioResp basicPortfolioResp = portfolioService.enrollBasicPortfolio(jobName);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.CREATED.value(), "기본 프토플리오가 생성되었습니다.", basicPortfolioResp));
    }

    /**
     * 포트폴리오 정보 조회
     * @param portfolioId
     * @return
     */
    @GetMapping("/portfolio/{portfolio_id}")
    public ResponseEntity<BasicResponse> getPortfolio(@PathVariable("portfolio_id") Long portfolioId) {
        PortfolioDTO.PortfolioInfoResp portfolioInfoResp = portfolioService.getPortfolio(portfolioId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "포트폴리오 정보 반환", portfolioInfoResp));
    }

    /**
     * 직무 별 포트폴리오 목록 조회
     * @param jobName
     * @param keyword
     * @param page
     * @return
     */
    @GetMapping("/portfolios")
    public ResponseEntity<BasicResponse> getPortfolios(@RequestParam("name") String jobName,
                                                       @RequestParam(value = "keyword", required = false) String keyword,
                                                       @RequestParam(value = "page", required = false) Integer page) {
        if(page == null || page < 1) {
            page = 1;
        }

        if(keyword == null) {
            keyword = "";
        }

        PortfolioDTO.PortfolioAndMemberListResp portfolioAndMemberListResp = portfolioService.getPortfolioList(jobName, page, keyword);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "직무별 포트폴리오 목록 반환", portfolioAndMemberListResp));
    }

    /**
     * 포트폴리오 About Me 정보 조회
     * @param portfolioId
     * @return
     */
    @GetMapping("/portfolio/{portfolio_id}/about-me")
    public ResponseEntity<BasicResponse> getPortfolioAboutMe(@PathVariable("portfolio_id") Long portfolioId) {
        PortfolioDTO.PortfolioAboutMeResp portfolioAboutMe = portfolioService.getPortfolioAboutMe(portfolioId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "포트폴리오 About Me 정보 반환", portfolioAboutMe));
    }

    /**
     * 사용자 포트폴리오 목록 조회
     * @return
     */
    @GetMapping("/member/portfolio")
    public ResponseEntity<BasicResponse> getMemberPortfolioList() {
        PortfolioDTO.PortfolioAndMemberListResp portfolioAndMemberListResp = portfolioService.getMemberPortfolioList();

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "내 포트폴리오 목록 조회", portfolioAndMemberListResp));
    }

    /**
     * 포트폴리오 수정
     * @param portfolioId
     * @param updateReq
     * @return
     */
    @PutMapping("/member/portfolio/{portfolio_id}")
    public ResponseEntity<BasicResponse> updatePortfolio(@PathVariable("portfolio_id") Long portfolioId,
                                                         @RequestBody PortfolioDTO.UpdateReq updateReq) {
        PortfolioDTO.PortfolioInfoResp portfolioInfoResp = portfolioService.updatePortfolio(portfolioId, updateReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "프토플리오가 수정되었습니다.", portfolioInfoResp));
    }

    /**
     * 포트폴리오 삭제
     * @param portfolioId
     * @return
     */
    @DeleteMapping("/member/portfolio/{portfolio_id}")
    public ResponseEntity<BasicResponse> deletePortfolio(@PathVariable("portfolio_id") Long portfolioId) {
        portfolioService.deletePortfolio(portfolioId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "포트폴리오가 삭제되었습니다."));
    }

}
