package com.poje.remind.service.member;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.config.jwt.JwtTokenProvider;
import com.poje.remind.config.jwt.TokenDTO;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.Member.dto.MemberDTO;
import com.poje.remind.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, String> redisTemplate;


    @Transactional
    public void signUp(MemberDTO.JoinReq joinReq) {
        memberRepository.findByLoginId(joinReq.getLoginId()).ifPresent((a) -> {
            throw new GlobalException(ErrorCode.LOGIN_ID_ALREADY_EXISTS);
        });

        Member member = Member.builder()
                .loginId(joinReq.getLoginId())
                .password(passwordEncoder.encode(joinReq.getPassword()))
                .nickName(joinReq.getNickName())
                .email(joinReq.getEmail())
                .phoneNum(joinReq.getPhoneNum())
                .gender(joinReq.getGender())
                .birth(joinReq.getBirth())
                .profileImg("DEFAULT_PROFILE_IMG")
                .role(RoleType.ROLE_USER)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public TokenDTO signIn(MemberDTO.LoginReq loginReq) {
        // 로그인 정보로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginReq.getLoginId(), loginReq.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDTO tokenDTO = tokenProvider.generateToken(authentication);

        redisTemplate.opsForValue().set(
                authentication.getName(),
                tokenDTO.getRefreshToken(),
                tokenProvider.getExpiration(tokenDTO.getRefreshToken()),
                TimeUnit.MILLISECONDS
        );

        return tokenDTO;
    }

    @Transactional
    public void logout(String accessToken) {
        if(!tokenProvider.validateToken(accessToken)) {
            throw new GlobalException(ErrorCode.TOKEN_NOT_VALIDATE);
        }

        Authentication authentication = tokenProvider.getAuthentication(accessToken);

        if(redisTemplate.opsForValue().get(authentication.getName()) != null) {
            redisTemplate.delete(authentication.getName());
        }

        redisTemplate.opsForValue().set(
                accessToken,
                "logout",
                tokenProvider.getExpiration(accessToken),
                TimeUnit.MILLISECONDS
        );
    }

    @Transactional
    public TokenDTO reissue(String accessToken, String refreshToken) {
        // refreshToken 만료 여부도 검사
        if(!tokenProvider.validateToken(refreshToken)) {
            throw new GlobalException(ErrorCode.TOKEN_NOT_VALIDATE);
        }

        Authentication authentication = tokenProvider.getAuthentication(accessToken);

        String savedRefreshToken = redisTemplate.opsForValue().get(authentication.getName());

        if(!refreshToken.equals(savedRefreshToken)) {
            throw new GlobalException(ErrorCode.REFRESH_TOKEN_NOT_MATCHED);
        }

        // 새로운 토큰 생성
        TokenDTO tokenDTO = tokenProvider.generateToken(authentication);

        redisTemplate.opsForValue().set(
                authentication.getName(),
                tokenDTO.getRefreshToken(),
                tokenProvider.getExpiration(tokenDTO.getRefreshToken()),
                TimeUnit.MILLISECONDS
        );

        return tokenDTO;
    }

}
