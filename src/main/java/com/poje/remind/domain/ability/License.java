package com.poje.remind.domain.ability;

import com.poje.remind.common.BaseEntity;
import com.poje.remind.domain.Member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "license")
@Entity
public class License extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "license_id")
    private Long id;

    private String name;
    private String issueInstitution;
    private LocalDate issueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    @Builder
    private License(String name, String issueInstitution, LocalDate issueDate, Member owner) {
        this.name = name;
        this.issueInstitution = issueInstitution;
        this.issueDate = issueDate;
        this.owner = owner;

        owner.getLicenseList().add(this);
    }

    public void updateLicense(String name, String issueInstitution, LocalDate issueDate) {
        this.name = name;
        this.issueInstitution = issueInstitution;
        this.issueDate = issueDate;
    }

}
