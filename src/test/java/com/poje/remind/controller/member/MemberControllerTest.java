package com.poje.remind.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.Member.dto.MemberDTO;
import com.poje.remind.service.member.MemberService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @MockBean
    BCryptPasswordEncoder passwordEncoder;


    @Test
    @DisplayName("로그인 아이디 중복 확인 테스트")
    void checkLoginIdDuplicated() throws Exception {
        // given
        String loginId = "testId";

        // when
        mockMvc.perform(get("/check-loginId")   // get 요청의 경우에는 csrf() 검증을 수행하지 않음
                        .param("loginId", loginId)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("사용 가능한 아이디 입니다."));

        // then
        verify(memberService).checkLoginIdDuplicated(Mockito.anyString());
    }

    @Test
    @DisplayName("사용자 정보 반환 테스트")
    void getMember() throws Exception {
        // given
        Member member = Member.builder()
                .loginId("testId")
                .password(passwordEncoder.encode("1234"))
                .nickName("tester01")
                .email("test@test.com")
                .phoneNum("01012345678")
                .gender("Male")
                .birth("240422")
                .profileImg("DEFAULT_PROFILE_IMG")
                .role(RoleType.ROLE_USER)
                .build();

        MemberDTO.MemberResp memberResp = MemberDTO.MemberResp.builder()
                .member(member)
                .build();

        // when
        when(memberService.getMember()).thenReturn(memberResp);

        mockMvc.perform(get("/member")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("사용자 정보 반환"))
                .andExpect(jsonPath("$.result.nickName").value("tester01"))
                .andExpect(jsonPath("$.result.email").value("test@test.com"))
                .andExpect(jsonPath("$.result.phoneNum").value("01012345678"))
                .andExpect(jsonPath("$.result.gender").value("Male"))
                .andExpect(jsonPath("$.result.birth").value("240422"))
                .andExpect(jsonPath("$.result.profileImg").value("DEFAULT_PROFILE_IMG"))
                .andExpect(jsonPath("$.result.academic").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.result.dept").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.result.gitHubLink").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.result.blogLink").value(Matchers.nullValue()));

        // then
        verify(memberService).getMember();
    }

    @Test
    @DisplayName("사용자 정보 수정 테스트")
    void updateMember() throws Exception {
        // given
        Member member = Member.builder()
                .loginId("testId")
                .password(passwordEncoder.encode("1234"))
                .nickName("newNickName")
                .email("test@test.com")
                .phoneNum("01012345678")
                .gender("Female")
                .birth("240422")
                .profileImg("DEFAULT_PROFILE_IMG")
                .role(RoleType.ROLE_USER)
                .academic("hanshin university")
                .dept("computer")
                .gitHubLink("github")
                .blogLink("blog")
                .build();

        MemberDTO.MemberResp memberResp = MemberDTO.MemberResp.builder()
                .member(member)
                .build();

        MemberDTO.UpdateReq updateReq = new MemberDTO.UpdateReq("newNickName", "test@test.com", "01012345678", "Female",
                "240422", "hanshin university", "computer", "github", "blog");

        given(memberService.updateMember(any(MemberDTO.UpdateReq.class))).willReturn(memberResp);

        // when
        mockMvc.perform(put("/member").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq))
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("회원 정보가 수정되었습니다."))
                .andExpect(jsonPath("$.result.nickName").value("newNickName"))
                .andExpect(jsonPath("$.result.email").value("test@test.com"))
                .andExpect(jsonPath("$.result.phoneNum").value("01012345678"))
                .andExpect(jsonPath("$.result.gender").value("Female"))
                .andExpect(jsonPath("$.result.birth").value("240422"))
                .andExpect(jsonPath("$.result.profileImg").value("DEFAULT_PROFILE_IMG"))
                .andExpect(jsonPath("$.result.academic").value("hanshin university"))
                .andExpect(jsonPath("$.result.dept").value("computer"))
                .andExpect(jsonPath("$.result.gitHubLink").value("github"))
                .andExpect(jsonPath("$.result.blogLink").value("blog"));

        // then
        verify(memberService).updateMember(refEq(updateReq));
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    void updatePassword() throws Exception {
        // given
        MemberDTO.PasswordUpdateReq passwordUpdateReq = new MemberDTO.PasswordUpdateReq("1234", "5678", "5678");

        // when
        mockMvc.perform(put("/member/password").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordUpdateReq))
                ).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("비밀번호가 변경되었습니다."))
                .andDo(print());

        // then
        verify(memberService).updatePassword(any(MemberDTO.PasswordUpdateReq.class));
    }
}