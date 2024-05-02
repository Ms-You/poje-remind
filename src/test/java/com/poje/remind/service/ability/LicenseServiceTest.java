package com.poje.remind.service.ability;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.License;
import com.poje.remind.domain.ability.dto.LicenseDTO;
import com.poje.remind.repository.ability.LicenseRepository;
import com.poje.remind.repository.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(LicenseService.class)
class LicenseServiceTest {

    @Autowired
    private LicenseService licenseService;

    @MockBean
    private LicenseRepository licenseRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    private Member member;

    @BeforeEach
    void setup() {
        member = Member.builder()
                .loginId("testId001")
                .password(passwordEncoder.encode("1234"))
                .nickName("tester001")
                .email("test@test.com")
                .phoneNum("01012345678")
                .gender("Male")
                .birth("240422")
                .profileImg("DEFAULT_PROFILE_IMG")
                .role(RoleType.ROLE_USER)
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(member.getLoginId());
        
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void end() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("자격증 등록 테스트")
    void enrollLicense() {
        // given
        LicenseDTO.CreateReq createReq =
                new LicenseDTO.CreateReq("정보처리기사", "한국산업인력공단", LocalDate.of(2022, 06, 18));

        // when
        when(memberRepository.findByLoginId(member.getLoginId())).thenReturn(Optional.of(member));
        when(licenseRepository.existsByOwnerAndName(member, createReq.getName())).thenReturn(false);

        licenseService.enrollLicense(createReq);

        // then
        verify(licenseRepository, times(1)).existsByOwnerAndName(member, createReq.getName());
        verify(licenseRepository, times(1)).save(any(License.class));
    }

    @Test
    @DisplayName("자격증 수정 테스트")
    void updateLicense() {
        // given
        License license = License.builder()
                .name("정보처리기사")
                .issueInstitution("한국산업인력공단")
                .issueDate(LocalDate.of(2022, 06, 18))
                .owner(member)
                .build();

        LicenseDTO.UpdateReq updateReq = new LicenseDTO.UpdateReq("정보처리기능사", "한국산업인력공단", LocalDate.of(2020, 07, 17));

        // when
        when(memberRepository.findByLoginId(member.getLoginId())).thenReturn(Optional.of(member));
        when(licenseRepository.findByOwnerAndName(member, updateReq.getName())).thenReturn(Optional.of(license));

        LicenseDTO.LicenseListResp licenseListResp = licenseService.updateLicense(updateReq);

        // then
        verify(licenseRepository, times(1)).findByOwnerAndName(member, updateReq.getName());
        assertThat(licenseListResp).isNotNull();
        assertThat(licenseListResp.getLicenseRespList()).hasSize(1);
        assertThat(licenseListResp.getLicenseRespList().get(0).getName()).isEqualTo(updateReq.getName());
    }

    @Test
    @DisplayName("자격증 수정 테스트 - 자격증 찾기 실패")
    void updateLicense_fail() {
        // given
        License license = License.builder()
                .name("정보처리기사")
                .issueInstitution("한국산업인력공단")
                .issueDate(LocalDate.of(2022, 06, 18))
                .owner(member)
                .build();

        LicenseDTO.UpdateReq updateReq = new LicenseDTO.UpdateReq("정보처리기능사", "한국산업인력공단", LocalDate.of(2020, 07, 17));

        // when
        when(memberRepository.findByLoginId(member.getLoginId())).thenReturn(Optional.of(member));
        when(licenseRepository.findByOwnerAndName(member, license.getName())).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            licenseService.updateLicense(updateReq);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LICENSE_NOT_FOUND);
    }

    @Test
    @DisplayName("자격증 목록 조회 테스트")
    void getLicenseList() {
        // given
        License license = License.builder()
                .name("정보처리기사")
                .issueInstitution("한국산업인력공단")
                .issueDate(LocalDate.of(2022, 06, 18))
                .owner(member)
                .build();

        // when
        when(memberRepository.findByLoginId(member.getLoginId())).thenReturn(Optional.of(member));

        LicenseDTO.LicenseListResp licenseListResp = licenseService.getLicenseList();

        // then
        assertThat(licenseListResp).isNotNull();
        assertThat(licenseListResp.getLicenseRespList()).hasSize(1);
        assertThat(licenseListResp.getLicenseRespList().get(0).getName()).isEqualTo(license.getName());
    }
}