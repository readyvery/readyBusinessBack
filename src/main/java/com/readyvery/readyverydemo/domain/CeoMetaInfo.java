package com.readyvery.readyverydemo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor
@Entity
@Builder
@Table(name = "INFOTAINMENT")
@AllArgsConstructor
@Slf4j
public class CeoMetaInfo extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ceo_meta_idx")
	private Long id;

	@Column(nullable = false, length = 45)
	private String storeName;

	@Column(nullable = false, length = 100)
	private String storeAddress;

	@Column(nullable = false, length = 45)
	private String registrationNumber;

}
