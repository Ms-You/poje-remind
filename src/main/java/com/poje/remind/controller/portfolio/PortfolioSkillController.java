package com.poje.remind.controller.portfolio;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.domain.portfolio.dto.PortfolioSkillDTO;
import com.poje.remind.service.portfolio.PortfolioSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PortfolioSkillController {

    private final PortfolioSkillService portfolioSkillService;

    /**
     * 포트폴리오 사용 기술 추가
     * @param portfolioId
     * @param createReq
     * @return
     */
    @PostMapping("/member/portfolio/{portfolio_id}/skill")
    public ResponseEntity<BasicResponse> enrollPortfolioSkill(@PathVariable("portfolio_id") Long portfolioId,
                                                              @RequestBody PortfolioSkillDTO.CreateReq createReq) {
        portfolioSkillService.enrollPortfolioSkill(portfolioId, createReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.CREATED.value(), "포트폴리오에 기술이 추가되었습니다."));
    }

    /**
     * 포트폴리오 사용 기술 수정 (추가 or 삭제)
     * @param portfolioId
     * @param updateReq
     * @return
     */
    @PutMapping("/member/portfolio/{portfolio_id}/skill")
    public ResponseEntity<BasicResponse> updatePortfolioSkill(@PathVariable("portfolio_id") Long portfolioId,
                                                              @RequestBody PortfolioSkillDTO.UpdateReq updateReq) {
        portfolioSkillService.updatePortfolioSkill(portfolioId, updateReq);

        List<PortfolioSkillDTO.PortfolioSkillListResp> portfolioSkillListRespList = portfolioSkillService.getPortfolioSkill(portfolioId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "사용 기술이 수정되었습니다.", portfolioSkillListRespList));
    }

    /**
     * 포트폴리오 사용 기술 조회
     * @param portfolioId
     * @return
     */
    @GetMapping("/portfolio/{portfolio_id}/skill")
    public ResponseEntity<BasicResponse> getPortfolioSkill(@PathVariable("portfolio_id") Long portfolioId) {
        List<PortfolioSkillDTO.PortfolioSkillListResp> pfSkillListRespList = portfolioSkillService.getPortfolioSkill(portfolioId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "포트폴리오 사용 기술 목록 반환", pfSkillListRespList));
    }
}
