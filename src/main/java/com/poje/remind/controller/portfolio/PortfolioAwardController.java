package com.poje.remind.controller.portfolio;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.domain.portfolio.dto.PortfolioAwardDTO;
import com.poje.remind.service.portfolio.PortfolioAwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PortfolioAwardController {

    private final PortfolioAwardService portfolioAwardService;

    /**
     * 포트폴리오 기본 수상 정보 생성
     * @param portfolioId
     * @return
     */
    @PostMapping("/member/portfolio/{portfolio_id}/award")
    public ResponseEntity<BasicResponse> enrollPortfolioAward(@PathVariable("portfolio_id") Long portfolioId) {
        PortfolioAwardDTO.PortfolioAwardResp portfolioAwardResp = portfolioAwardService.enrollPortfolioAward(portfolioId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.CREATED.value(), "기본 수상 정보가 등록되었습니다.", portfolioAwardResp));
    }

    /**
     * 포트폴리오 수상 정보 조회
     * @param portfolioId
     * @return
     */
    @GetMapping("/portfolio/{portfolio_id}/award")
    public ResponseEntity<BasicResponse> getPortfolioAward(@PathVariable("portfolio_id") Long portfolioId) {
        List<PortfolioAwardDTO.PortfolioAwardResp> portfolioAwardRespList = portfolioAwardService.getPortfolioAwardList(portfolioId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "포트폴리오 수상 정보 목록 조회", portfolioAwardRespList));
    }

    /**
     * 포트폴리오 수상 정보 수정
     * @param portfolioAwardId
     * @param updateReq
     * @return
     */
    @PutMapping("/member/portfolio/award/{award_id}")
    public ResponseEntity<BasicResponse> updatePortfolioAward(@PathVariable("award_id") Long portfolioAwardId, @RequestBody PortfolioAwardDTO.UpdateReq updateReq) {
        PortfolioAwardDTO.PortfolioAwardResp portfolioAwardResp = portfolioAwardService.updatePortfolioAward(portfolioAwardId, updateReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "수상 정보가 수정되었습니다.", portfolioAwardResp));
    }

    /**
     * 포트폴리오 수상 정보 삭제
     * @param portfolioAwardId
     * @return
     */
    @DeleteMapping("/member/portfolio/award/{award_id}")
    public ResponseEntity<BasicResponse> deletePortfolioAward(@PathVariable("award_id") Long portfolioAwardId) {
        portfolioAwardService.deletePortfolioAward(portfolioAwardId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "수상 정보가 삭제되었습니다."));
    }
}
