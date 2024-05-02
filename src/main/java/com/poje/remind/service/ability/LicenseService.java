package com.poje.remind.service.ability;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.config.SecurityUtil;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.ability.License;
import com.poje.remind.domain.ability.dto.LicenseDTO;
import com.poje.remind.repository.ability.LicenseRepository;
import com.poje.remind.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void enrollLicense(LicenseDTO.CreateReq createReq) {
        Member owner = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        if(licenseRepository.existsByOwnerAndName(owner, createReq.getName())) {
            throw new GlobalException(ErrorCode.LICENCE_ALREADY_ENROLLED);
        }

        License license = License.builder()
                .name(createReq.getName())
                .issueInstitution(createReq.getIssueInstitution())
                .issueDate(createReq.getIssueDate())
                .owner(owner)
                .build();

        licenseRepository.save(license);
    }

    @Transactional
    public LicenseDTO.LicenseListResp updateLicense(LicenseDTO.UpdateReq updateReq) {
        Member owner = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        License license = licenseRepository.findByOwnerAndName(owner, updateReq.getName()).orElseThrow(
                () -> new GlobalException(ErrorCode.LICENSE_NOT_FOUND)
        );

        license.updateLicense(updateReq.getName(), updateReq.getIssueInstitution(), updateReq.getIssueDate());

        List<License> licenseList = owner.getLicenseList();
        List<LicenseDTO.LicenseResp> licenseRespList = licenseList.stream()
                .map(licenseInfo -> new LicenseDTO.LicenseResp(licenseInfo.getName(), licenseInfo.getIssueInstitution(), licenseInfo.getIssueDate()))
                .collect(Collectors.toList());

        return new LicenseDTO.LicenseListResp(licenseRespList);
    }

    @Transactional(readOnly = true)
    public LicenseDTO.LicenseListResp getLicenseList() {
        Member owner = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        List<License> licenseList = owner.getLicenseList();
        List<LicenseDTO.LicenseResp> licenseRespList = licenseList.stream()
                .map(license -> new LicenseDTO.LicenseResp(license.getName(), license.getIssueInstitution(), license.getIssueDate()))
                .collect(Collectors.toList());

        return new LicenseDTO.LicenseListResp(licenseRespList);
    }
}
