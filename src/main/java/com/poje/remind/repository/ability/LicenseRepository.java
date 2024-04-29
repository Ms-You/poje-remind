package com.poje.remind.repository.ability;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.ability.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {
    boolean existsByOwnerAndName(Member owner, String name);

    Optional<License> findByOwnerAndName(Member owner, String name);
}
