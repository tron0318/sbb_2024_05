package com.sbs.sbb.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<SiteUser, Integer> {
    @Transactional
    @Modifying
    @Query(value="ALTER TABLE site_user AUTO_INCREMENT = 1", nativeQuery = true)
    void clearAutoIncrement();
}