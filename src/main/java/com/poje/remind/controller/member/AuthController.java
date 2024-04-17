package com.poje.remind.controller.member;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.config.jwt.TokenDTO;
import com.poje.remind.domain.Member.dto.MemberDTO;
import com.poje.remind.service.member.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * @param joinReq
     * @return
     */
    @PostMapping
    public ResponseEntity<BasicResponse> signUp(@RequestBody MemberDTO.JoinReq joinReq) {
        authService.signUp(joinReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.CREATED.value(), "회원가입이 완료되었습니다."));
    }

    /**
     * 로그인
     * @param loginReq
     * @param response
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<BasicResponse> signIn(@RequestBody MemberDTO.LoginReq loginReq, HttpServletResponse response) {
        TokenDTO tokenDTO = authService.signIn(loginReq);

        Cookie refreshTokenCookie = new Cookie("RefreshToken", tokenDTO.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true); // JS를 통한 접근 방지
        refreshTokenCookie.setSecure(true);   // HTTPS를 통해서만 쿠키 전송
        refreshTokenCookie.setPath("/");      // 쿠키를 전송할 요청의 경로
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키의 유효 시간 - 7일

        response.setHeader("Authorization", "Bearer" + tokenDTO.getAccessToken());
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "로그인 성공"));
    }

    /**
     * 로그아웃
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<BasicResponse> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            authService.logout(bearerToken.substring(7));
        }

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "로그아웃 되었습니다."));
    }

    /**
     * 토큰 재발급
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/reissue")
    public ResponseEntity<BasicResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("RefreshToken");

        TokenDTO tokenDTO = authService.reissue(accessToken, refreshToken);

        Cookie refreshTokenCookie = new Cookie("RefreshToken", tokenDTO.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true); // JS를 통한 접근 방지
        refreshTokenCookie.setSecure(true);   // HTTPS를 통해서만 쿠키 전송
        refreshTokenCookie.setPath("/");      // 쿠키를 전송할 요청의 경로
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키의 유효 시간 - 7일

        response.setHeader("Authorization", "Bearer" + tokenDTO.getAccessToken());
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "성공적으로 재발급 되었습니다."));
    }


}
