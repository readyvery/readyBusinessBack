package com.readyvery.readyverydemo.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.readyvery.readyverydemo.domain.CeoMetaInfo;

public interface CeoMetaRepository extends JpaRepository<CeoMetaInfo, Long> {
}
