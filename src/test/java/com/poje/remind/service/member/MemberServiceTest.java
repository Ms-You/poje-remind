package com.poje.remind.service.member;

import com.poje.remind.common.GlobalException;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.Member.dto.MemberDTO;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(MemberService.class)
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeTest() {
        String loginId = "testId001";

        // SecurityContext에 Authentication 객체 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(loginId);

    }

    @AfterEach
    void endTest() {
        // 테스트 종료 후 SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("아이디 중복 체크 테스트")
    void checkLoginIdDuplicated() {
        // given
        String loginId = "testId001";

        // when
        when(memberRepository.findByLoginId(loginId))
                .thenReturn(Optional.of(Member.builder().build()));

        // then
        assertThrows(GlobalException.class, () -> {
            memberService.checkLoginIdDuplicated(loginId);
        });
    }

    @Test
    @DisplayName("사용자 정보 반환 테스트")
    void getMember() {
        // given
        String loginId = "testId001";

        Member member = Member.builder()
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

        // when
        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.of(member));
        MemberDTO.MemberResp memberResp = memberService.getMember();

        // then
        assertThat(memberResp).isNotNull();
        assertThat(memberResp.getNickName()).isEqualTo(member.getNickName());
        assertThat(memberResp.getGender()).isEqualTo(member.getGender());
    }

    @Test
    @DisplayName("사용자 정보 수정 테스트")
    void updateMember() {
        // given
        String loginId = "testId001";

        Member member = Member.builder()
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

        MemberDTO.UpdateReq updateReq = new MemberDTO.UpdateReq("tester002", "test@test.com", "01012345678", "Female",
                "240422", "hanshin univ", "computer", "github", "blog");

        // when
        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.of(member));

        MemberDTO.MemberResp memberResp = memberService.updateMember(updateReq);

        // then
        assertThat(memberResp).isNotNull();
        assertThat(memberResp.getNickName()).isEqualTo("tester002");
        assertThat(memberResp.getGender()).isEqualTo("Female");
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    void updatePassword() {
        // given
        String loginId = "testId001";
        String password = "password";
        String newPassword = "encodedPassword";

        Member member = Member.builder()
                .loginId("testId001")
                .password(password)
                .nickName("tester001")
                .email("test@test.com")
                .phoneNum("01012345678")
                .gender("Male")
                .birth("240422")
                .profileImg("DEFAULT_PROFILE_IMG")
                .role(RoleType.ROLE_USER)
                .build();

        MemberDTO.PasswordUpdateReq passwordUpdateReq =
                new MemberDTO.PasswordUpdateReq(password, newPassword, newPassword);

        // when
        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.of(member));

        // Mock 객체인 passwordEncoder가 encode 함수를 호출할 때 "encodedPassword"를 반환하도록 설정
        when(passwordEncoder.encode(anyString())).thenReturn(newPassword);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        memberService.updatePassword(passwordUpdateReq);

        // then
        verify(memberRepository, times(1)).findByLoginId(loginId);
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());

        assertTrue(passwordEncoder.matches(newPassword, member.getPassword()));
    }

}