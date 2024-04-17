package com.poje.remind.domain.ability;

import com.poje.remind.common.BaseEntity;
import com.poje.remind.domain.portfolio.Portfolio;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "job")
@Entity
public class Job extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "job")
    private List<Portfolio> portfolioList = new ArrayList<>();

    @Builder
    private Job(String name) {
        this.name = name;
    }
}
