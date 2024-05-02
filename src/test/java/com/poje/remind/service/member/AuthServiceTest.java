package com.poje.remind.service.member;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.config.jwt.JwtTokenProvider;
import com.poje.remind.config.jwt.TokenDTO;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.dto.MemberDTO;
import com.poje.remind.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(AuthService.class)
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setup() {
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(any())).thenReturn(new TestingAuthenticationToken("user", "password", "ROLE_USER"));

        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void signUp() {
        // given
        MemberDTO.JoinReq joinReq = new MemberDTO.JoinReq("testId", "1234", "1234",
                "tester01", "test@test.com", "01012345678", "Male", "240422");

        // when
        authService.signUp(joinReq);

        // then
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 테스트")
    void signIn() {
        // given
        String loginId = "testId001";
        String password = "password";
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        long REFRESH_TOKEN_EXPIRE_TIME = 1000 *60 * 60 * 24 * 7;
        MemberDTO.LoginReq loginReq = new MemberDTO.LoginReq(loginId, password);

        TokenDTO tokenDTO = new TokenDTO(accessToken, refreshToken);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginReq.getLoginId(), loginReq.getPassword());
        Authentication authentication = mock(Authentication.class);

        // when
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(authentication.getName()).thenReturn(loginReq.getLoginId());
        when(tokenProvider.generateToken(authentication)).thenReturn(tokenDTO);
        when(tokenProvider.getExpiration(tokenDTO.getRefreshToken())).thenReturn(REFRESH_TOKEN_EXPIRE_TIME);

        TokenDTO loginTokenDTO = authService.signIn(loginReq);

        // then
        assertThat(loginTokenDTO.getAccessToken()).isEqualTo(tokenDTO.getAccessToken());
        assertThat(loginTokenDTO.getRefreshToken()).isEqualTo(tokenDTO.getRefreshToken());
        verify(valueOperations, times(1)).set(
                eq(authentication.getName()),
                eq(tokenDTO.getRefreshToken()),
                eq(REFRESH_TOKEN_EXPIRE_TIME),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logout() {
        // given
        String loginId = "testId001";
        String accessToken = "AccessToken";
        long expirationTime = 1000 * 60 * 60;

        Authentication authentication = mock(Authentication.class);

        // when
        when(tokenProvider.validateToken(accessToken)).thenReturn(true);
        when(authentication.getName()).thenReturn(loginId);
        when(redisTemplate.opsForValue().get(authentication.getName())).thenReturn(loginId);
        when(tokenProvider.getAuthentication(accessToken)).thenReturn(authentication);
        when(tokenProvider.getExpiration(accessToken)).thenReturn(expirationTime);

        authService.logout("AccessToken");

        // then
        verify(redisTemplate, times(1)).delete(anyString());
        verify(valueOperations, times(1)).set(eq(accessToken), eq("logout"), eq(expirationTime), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("토큰 재발급 테스트 - 성공")
    void reissue() {
        // given
        String loginId = "testId001";
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";
        long REFRESH_TOKEN_EXPIRE_TIME = 1000 *60 * 60 * 24 * 7;

        Authentication authentication = mock(Authentication.class);
        TokenDTO reissuedTokenDTO = new TokenDTO(newAccessToken, newRefreshToken);

        // when
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getAuthentication(accessToken)).thenReturn(authentication);
        when(authentication.getName()).thenReturn(loginId);
        when(redisTemplate.opsForValue().get(authentication.getName())).thenReturn(refreshToken);
        when(tokenProvider.generateToken(authentication)).thenReturn(reissuedTokenDTO);
        when(tokenProvider.getExpiration(newRefreshToken)).thenReturn(REFRESH_TOKEN_EXPIRE_TIME);

        TokenDTO resultTokenDTO = authService.reissue(accessToken, refreshToken);

        // then
        assertThat(resultTokenDTO.getAccessToken()).isEqualTo(reissuedTokenDTO.getAccessToken());
        assertThat(resultTokenDTO.getRefreshToken()).isEqualTo(reissuedTokenDTO.getRefreshToken());

        verify(valueOperations, times(1)).set(eq(authentication.getName()),
                eq(reissuedTokenDTO.getRefreshToken()),
                eq(REFRESH_TOKEN_EXPIRE_TIME),
                eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("토큰 재발급 테스트 - 리프레시 토큰 만료")
    void reissue_refresh_token_expired() {
        // given
        String accessToken = "AccessToken";
        String refreshToken = "ExpiredRefreshToken";

        // when
        when(tokenProvider.validateToken(refreshToken)).thenReturn(false);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            authService.reissue(accessToken, refreshToken);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOKEN_NOT_VALIDATE);
    }

    @Test
    @DisplayName("저장된 리프레시 토큰과 전달받은 리프레시 토큰 불일치")
    void reissue_refresh_token_not_matched() {
        // given
        String loginId = "testId001";
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        String savedRefreshToken = "AnotherRefreshToken";

        Authentication authentication = mock(Authentication.class);

        // when
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getAuthentication(accessToken)).thenReturn(authentication);
        when(authentication.getName()).thenReturn(loginId);
        when(redisTemplate.opsForValue().get(authentication.getName())).thenReturn(savedRefreshToken);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            authService.reissue(accessToken, refreshToken);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_NOT_MATCHED);
    }
}