package com.poje.remind.controller.member;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.domain.Member.dto.MemberDTO;
import com.poje.remind.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 가입 시 로그인 아이디 중복 체크
     * @param loginId
     * @return
     */
    @GetMapping("/check-loginId")
    public ResponseEntity<BasicResponse> checkLoginIdDuplicated(@RequestParam(name = "loginId") String loginId) {
        memberService.checkLoginIdDuplicated(loginId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "사용 가능한 아이디 입니다."));
    }

    /**
     * 사용자 정보 반환
     * @return
     */
    @GetMapping("/member")
    public ResponseEntity<BasicResponse> getMember() {
        MemberDTO.MemberResp memberResp = memberService.getMember();

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "사용자 정보 반환", memberResp));
    }

    /**
     * 사용자 정보 수정
     * @return
     */
    @PutMapping("/member")
    public ResponseEntity<BasicResponse> updateMember(@RequestBody MemberDTO.UpdateReq updateReq) {
        MemberDTO.MemberResp memberResp = memberService.updateMember(updateReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "회원 정보가 수정되었습니다.", memberResp));
    }

    /**
     * 비밀번호 변경
     * @param passwordUpdateReq
     * @return
     */
    @PutMapping("/member/password")
    public ResponseEntity<BasicResponse> updatePassword(@RequestBody MemberDTO.PasswordUpdateReq passwordUpdateReq) {
        memberService.updatePassword(passwordUpdateReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "비밀번호가 변경되었습니다."));
    }
}
