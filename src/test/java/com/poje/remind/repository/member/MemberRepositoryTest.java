package com.poje.remind.repository.member;

import com.poje.remind.domain.Member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest    // 테스트가 끝나면 롤백
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Member 저장 테스트")
    void save() {
        // given
        Member member1 = Member.builder()
                .nickName("tester001")
                .gender("Male")
                .build();

        Member member2 = Member.builder()
                .nickName("tester002")
                .gender("Female")
                .build();

        // when
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        // then
        assertThat(savedMember1.getNickName()).isEqualTo(member1.getNickName());
        assertThat(savedMember1.getGender()).isEqualTo(member1.getGender());
        assertThat(savedMember2.getNickName()).isEqualTo(member2.getNickName());
        assertThat(savedMember2.getGender()).isEqualTo(member2.getGender());
    }

    @Test
    @DisplayName("Member 찾기 테스트")
    void findById() {
        // given
        Member member = Member.builder()
                .loginId("testId001")
                .nickName("tester001")
                .gender("Male")
                .build();

        Member savedMember = memberRepository.save(member);

        // when
        Optional<Member> findMember = memberRepository.findById(savedMember.getId());

        // then
        assertThat(findMember).isNotEmpty();
        assertThat(findMember.get().getLoginId()).isEqualTo(member.getLoginId());
        assertThat(findMember.get().getNickName()).isEqualTo(member.getNickName());
        assertThat(findMember.get().getGender()).isEqualTo(member.getGender());
    }

    @Test
    @DisplayName("Member 목록 조회 테스트")
    void findAll() {
        // given
        Member member1 = Member.builder()
                .loginId("testId001")
                .nickName("tester001")
                .gender("Male")
                .build();

        Member member2 = Member.builder()
                .loginId("testId002")
                .nickName("tester002")
                .gender("Female")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> memberList = memberRepository.findAll();

        // then
        assertThat(memberList.size()).isEqualTo(2);

        assertThat(memberList.get(0).getLoginId()).isEqualTo("testId001");
        assertThat(memberList.get(0).getNickName()).isEqualTo("tester001");
        assertThat(memberList.get(1).getLoginId()).isEqualTo("testId002");
        assertThat(memberList.get(1).getNickName()).isEqualTo("tester002");
    }

    @Test
    @DisplayName("로그인 아이디로 Member 찾기 테스트")
    void findByLoginId() {
        // given
        String loginId = "testId";
        Member member = Member.builder()
                .loginId(loginId)
                .build();

        memberRepository.save(member);

        // when
        Optional<Member> savedMember = memberRepository.findByLoginId(loginId);

        // then
        assertThat(savedMember).isNotEmpty();
        assertThat(savedMember.get().getLoginId()).isEqualTo(loginId);
    }

    @Test
    @DisplayName("Member 수정 테스트")
    void updateMember() {
        // given
        Member member = Member.builder()
                .loginId("testId001")
                .nickName("tester001")
                .gender("Male")
                .build();

        Member savedMember = memberRepository.save(member);

        // when
        savedMember.updateInfo("tester002", "test@test.com", "01012345678", "Female", "hanshin univ",
                "computer", "240422", "DEFAULT_PROFILE_IMG", "github", "blog");

        Member updatedMember = memberRepository.save(savedMember);

        // then
        assertThat(updatedMember.getLoginId()).isEqualTo("testId001");
        assertThat(updatedMember.getNickName()).isEqualTo("tester002");
        assertThat(updatedMember.getEmail()).isEqualTo("test@test.com");
        assertThat(updatedMember.getGender()).isEqualTo("Female");
    }


    @Test
    @DisplayName("Member 삭제 테스트")
    void delete() {
        // given
        Member member = Member.builder()
                .loginId("testId001")
                .nickName("tester001")
                .gender("Male")
                .build();

        Member savedMember = memberRepository.save(member);

        // when
        memberRepository.delete(savedMember);

        // then
        Optional<Member> deleteMember = memberRepository.findByLoginId("testId001");
        assertThat(deleteMember).isEmpty();
    }

}