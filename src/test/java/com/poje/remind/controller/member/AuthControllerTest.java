package com.poje.remind.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poje.remind.config.jwt.TokenDTO;
import com.poje.remind.domain.Member.dto.MemberDTO;
import com.poje.remind.service.member.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean   // DI 받는 객체를 Mock 객체로 생성해서 사용할 수 있음
    AuthService authService;

    @Test
    @DisplayName("회원가입 테스트")
    void signUp() throws Exception {
        // given
        MemberDTO.JoinReq joinReq = new MemberDTO.JoinReq("testId", "1234", "1234",
                "tester01", "test@test.com", "01012345678", "Male", "240422");

        // when
        mockMvc.perform(post("/auth").with(csrf())  // 권한 문제로 csrf()가 없으면 403 에러 발생
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinReq))
        ).andExpect(status().isOk());

        // then
        verify(authService).signUp(Mockito.any(MemberDTO.JoinReq.class));   // 해당 메서드가 예상대로 호출되었는지 확인
    }

    @Test
    @DisplayName("로그인 테스트")
    void signIn() throws Exception {
        // given
        MemberDTO.LoginReq loginReq = new MemberDTO.LoginReq("testId", "1234");
        TokenDTO tokenDTO = new TokenDTO("AccessToken", "RefreshToken");

        // when
        Mockito.when(authService.signIn(Mockito.any(MemberDTO.LoginReq.class))).thenReturn(tokenDTO);

        mockMvc.perform(post("/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq))
        ).andExpect(status().isOk());

        // then
        verify(authService).signIn(Mockito.any(MemberDTO.LoginReq.class));
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logout() throws Exception {
        // given
        String accessToken = "AccessToken";

        // when
        mockMvc.perform(post("/auth/logout").with(csrf())
                .header("Authorization", "Bearer " + accessToken)
        ).andExpect(status().isOk());

        // then
        verify(authService).logout(Mockito.anyString());
    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void reissue() throws Exception {
        // given
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        TokenDTO tokenDTO = new TokenDTO(accessToken, refreshToken);

        // when
        Mockito.when(authService.reissue(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(tokenDTO);

        mockMvc.perform(post("/auth/reissue").with(csrf())
                .header("Authorization", "Bearer " + accessToken)
                .header("RefreshToken", refreshToken)
        ).andExpect(status().isOk());

        // then
        verify(authService).reissue(Mockito.anyString(), Mockito.anyString());
    }
}