package com.poje.remind.controller.portfolio;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.domain.portfolio.dto.PortfolioSkillDTO;
import com.poje.remind.service.portfolio.PortfolioSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PortfolioSkillController {

    private final PortfolioSkillService portfolioSkillService;

    /**
     * 포트폴리오 사용 기술 수정 (추가 or 삭제)
     * @param portfolioId
     * @param updateReqList
     * @return
     */
    @PutMapping("/member/portfolio/{portfolio_id}/skill")
    public ResponseEntity<EntityModel<BasicResponse>> updatePortfolioSkill(@PathVariable("portfolio_id") Long portfolioId,
                                                                          @RequestBody PortfolioSkillDTO.UpdateReqList updateReqList) {
        portfolioSkillService.updatePortfolioSkill(portfolioId, updateReqList);

        BasicResponse basicResponse = new BasicResponse(HttpStatus.OK.value(), "사용 기술이 수정되었습니다.");

        EntityModel<BasicResponse> model = EntityModel.of(basicResponse);

        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(this.getClass()).getPortfolioSkill(portfolioId)
        );
        model.add(linkTo.withRel("포트폴리오 사용 기술 조회 링크"));

        return ResponseEntity.ok(model);
    }

    /**
     * 포트폴리오 사용 기술 조회
     * @param portfolioId
     * @return
     */
    @GetMapping("/portfolio/{portfolio_id}/skill")
    public ResponseEntity<BasicResponse> getPortfolioSkill(@PathVariable("portfolio_id") Long portfolioId) {
        List<PortfolioSkillDTO.PortfolioSKillResp> pfSkillListRespList = portfolioSkillService.getPortfolioSkill(portfolioId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "포트폴리오 사용 기술 목록 반환", pfSkillListRespList));
    }
}
