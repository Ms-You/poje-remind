package com.poje.remind.controller.ability;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.domain.ability.dto.LicenseDTO;
import com.poje.remind.service.ability.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/member")
@RestController
public class LicenseController {

    private final LicenseService licenseService;

    /**
     * 자격증 등록
     * @param createReq
     * @return
     */
    @PostMapping("/license")
    public ResponseEntity<BasicResponse> enrollLicense(@RequestBody LicenseDTO.CreateReq createReq) {
        licenseService.enrollLicense(createReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.CREATED.value(), "자격증 정보가 등록되었습니다."));
    }

    /**
     * 자격증 수정
     * @param updateReq
     * @return
     */
    @PutMapping("/license")
    public ResponseEntity<BasicResponse> updateLicense(LicenseDTO.UpdateReq updateReq) {
        LicenseDTO.LicenseListResp licenseListResp = licenseService.updateLicense(updateReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "자격증 정보가 수정되었습니다."));
    }

    /**
     * 자격증 목록 반환
     * @return
     */
    @GetMapping("/license")
    public ResponseEntity<BasicResponse> licenseList() {
        LicenseDTO.LicenseListResp licenseListResp = licenseService.getLicenseList();

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "자격증 목록 반환", licenseListResp));
    }
}
