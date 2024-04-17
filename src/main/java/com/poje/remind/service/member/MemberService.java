package com.poje.remind.service.member;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.config.SecurityUtil;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.dto.MemberDTO;
import com.poje.remind.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public void checkLoginIdDuplicated(String loginId) {
        memberRepository.findByLoginId(loginId).ifPresent(a -> {
            throw new GlobalException(ErrorCode.LOGIN_ID_ALREADY_EXISTS);
        });
    }

    @Transactional(readOnly = true)
    public MemberDTO.MemberResp getMember() {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        return MemberDTO.MemberResp.builder()
                .member(member)
                .build();
    }

    @Transactional
    public MemberDTO.MemberResp updateMember(MemberDTO.UpdateReq updateReq) {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        member.updateInfo(updateReq.getNickName(), updateReq.getEmail(),
                updateReq.getPhoneNum(), updateReq.getGender(),
                updateReq.getAcademic(), updateReq.getDept(),
                updateReq.getBirth(), "profileImg",
                updateReq.getGitHubLink(), updateReq.getBlogLink());

        memberRepository.save(member);  // 명시

        return MemberDTO.MemberResp.builder()
                .member(member)
                .build();
    }

    @Transactional
    public void updatePassword(MemberDTO.PasswordUpdateReq passwordUpdateReq) {
        Member member = memberRepository.findByLoginId(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND)
        );

        if(!passwordEncoder.matches(passwordUpdateReq.getExistsPassword(), member.getPassword()) ||
                !passwordUpdateReq.getNewPassword().equals(passwordUpdateReq.getConfirmNewPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        member.updatePassword(passwordUpdateReq.getNewPassword());
    }

}
