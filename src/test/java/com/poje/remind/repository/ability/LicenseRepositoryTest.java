package com.poje.remind.repository.ability;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.License;
import com.poje.remind.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LicenseRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LicenseRepository licenseRepository;

    @MockBean
    BCryptPasswordEncoder passwordEncoder;

    @Nested
    class WithRequiredObj {
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

            memberRepository.save(member);
        }

        @Test
        @DisplayName("License 저장 테스트")
        void save() {
            // given
            License license1 = License.builder()
                    .name("정보처리기사")
                    .issueInstitution("한국산업인력공단")
                    .issueDate(LocalDate.of(2022, 06, 22))
                    .owner(member)
                    .build();

            License license2 = License.builder()
                    .name("정보처리기능사")
                    .issueInstitution("한국산업인력공단")
                    .issueDate(LocalDate.of(2020, 07, 17))
                    .owner(member)
                    .build();

            // when
            License savedLicense1 = licenseRepository.save(license1);
            License savedLicense2 = licenseRepository.save(license2);

            // then
            assertThat(savedLicense1.getName()).isEqualTo(license1.getName());
            assertThat(savedLicense1.getIssueInstitution()).isEqualTo(license1.getIssueInstitution());
            assertThat(savedLicense1.getIssueDate()).isEqualTo(license1.getIssueDate());
            assertThat(savedLicense2.getName()).isEqualTo(license2.getName());
            assertThat(savedLicense2.getIssueInstitution()).isEqualTo(license2.getIssueInstitution());
            assertThat(savedLicense2.getIssueDate()).isEqualTo(license2.getIssueDate());
        }

        @Test
        @DisplayName("License 찾기 테스트")
        void findById() {
            // given
            License license = License.builder()
                    .name("정보처리기사")
                    .issueInstitution("한국산업인력공단")
                    .issueDate(LocalDate.of(2022, 06, 22))
                    .owner(member)
                    .build();

            License savedLicense = licenseRepository.save(license);

            // when
            Optional<License> findLicense = licenseRepository.findById(savedLicense.getId());

            // then
            assertThat(findLicense).isNotEmpty();
            assertThat(findLicense.get().getName()).isEqualTo(license.getName());
            assertThat(findLicense.get().getIssueInstitution()).isEqualTo(license.getIssueInstitution());
            assertThat(findLicense.get().getIssueDate()).isEqualTo(license.getIssueDate());
        }

        @Test
        @DisplayName("License 목록 조회 테스트")
        void findAll() {
            // given
            License license1 = License.builder()
                    .name("정보처리기사")
                    .issueInstitution("한국산업인력공단")
                    .issueDate(LocalDate.of(2022, 06, 22))
                    .owner(member)
                    .build();

            License license2 = License.builder()
                    .name("정보처리기능사")
                    .issueInstitution("한국산업인력공단")
                    .issueDate(LocalDate.of(2020, 07, 17))
                    .owner(member)
                    .build();

            licenseRepository.save(license1);
            licenseRepository.save(license2);

            // when
            List<License> licenseList = licenseRepository.findAll();

            // then
            assertThat(licenseList.size()).isEqualTo(2);
            assertThat(licenseList.get(0).getName()).isEqualTo(license1.getName());
            assertThat(licenseList.get(0).getIssueInstitution()).isEqualTo(license1.getIssueInstitution());
            assertThat(licenseList.get(0).getIssueDate()).isEqualTo(license1.getIssueDate());
            assertThat(licenseList.get(1).getName()).isEqualTo(license2.getName());
            assertThat(licenseList.get(1).getIssueInstitution()).isEqualTo(license2.getIssueInstitution());
            assertThat(licenseList.get(1).getIssueDate()).isEqualTo(license2.getIssueDate());
        }

        @Test
        @DisplayName("소유자와 자격증 이름으로 License가 존재하는지 확인 테스트")
        void existsByOwnerAndName() {
            // given
            License license = License.builder()
                    .name("정보처리기사")
                    .issueInstitution("한국산업인력공단")
                    .issueDate(LocalDate.of(2022, 06, 22))
                    .owner(member)
                    .build();

            licenseRepository.save(license);

            // when
            boolean isLicenseExists = licenseRepository.existsByOwnerAndName(member, license.getName());

            // then
            assertThat(isLicenseExists).isTrue();
        }

        @Test
        @DisplayName("소유자와 자격증 이름으로 License 찾기 테스트")
        void findByOwnerAndName() {
            // given
            License license = License.builder()
                    .name("정보처리기사")
                    .issueInstitution("한국산업인력공단")
                    .issueDate(LocalDate.of(2022, 06, 22))
                    .owner(member)
                    .build();

            licenseRepository.save(license);

            // when
            Optional<License> findLicense = licenseRepository.findByOwnerAndName(member, license.getName());

            // then
            assertThat(findLicense).isNotEmpty();
            assertThat(findLicense.get().getName()).isEqualTo("정보처리기사");
            assertThat(findLicense.get().getIssueInstitution()).isEqualTo("한국산업인력공단");
            assertThat(findLicense.get().getIssueDate()).isEqualTo("2022-06-22");
        }

        @Test
        @DisplayName("License 수정 테스트")
        void updateLicense() {
            // given
            License license = License.builder()
                    .name("정보처리기사")
                    .issueInstitution("한국산업인력공단")
                    .issueDate(LocalDate.of(2022, 06, 22))
                    .owner(member)
                    .build();

            License savedLicense = licenseRepository.save(license);

            // when
            savedLicense.updateLicense("정보처리기능사", "한국산업인력공단", LocalDate.of(2020, 07, 17));

            License updatedLicense = licenseRepository.save(savedLicense);

            // then
            assertThat(updatedLicense.getName()).isEqualTo("정보처리기능사");
            assertThat(updatedLicense.getIssueDate()).isEqualTo("2020-07-17");
        }

        @Test
        @DisplayName("License 삭제 테스트")
        void delete() {
            // given
            License license = License.builder()
                    .name("정보처리기사")
                    .issueInstitution("한국산업인력공단")
                    .issueDate(LocalDate.of(2022, 06, 22))
                    .owner(member)
                    .build();

            License savedLicense = licenseRepository.save(license);

            // when
            licenseRepository.delete(savedLicense);

            // then
            Optional<License> findLicense = licenseRepository.findById(savedLicense.getId());
            assertThat(findLicense).isEmpty();
        }
    }

}