package com.readyvery.readyverydemo.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "STORE")
@AllArgsConstructor
@Slf4j
@Builder
public class Store extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "store_idx")
	private Long id;

	//가게 이름
	@Column(nullable = false)
	private String name;

	//가게 주소
	@Column(nullable = false)
	private String address;

	//가게 전화번호
	@Column(nullable = false)
	private String phone;

	//가게 영업시간
	@Column(nullable = false)
	private String time;

	//가게 영업중 여부
	@Column(nullable = false, columnDefinition = "BOOLEAN default true")
	private boolean status;

	//가게 광고 이미지
	@Column
	private String adImgUrl;

	// 가게 영어 이름
	@Column
	private String engName;

	// 가게 서브 이름
	@Column
	private String subName;

	// 가게 이벤트 메세지
	@Column
	private String eventMessage;

	//가게 등급
	@Column
	@Enumerated(EnumType.STRING)
	private Grade grade;

	@Column
	private String account;

	//가게 사장님 연관관계 매핑
	@OneToMany(mappedBy = "store")
	private List<CeoInfo> ceoInfos = new ArrayList<CeoInfo>();

	//가게 메뉴 카테고리 연관관계 매핑
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	private List<FoodieCategory> foodieCategories = new ArrayList<FoodieCategory>();

	//가게 메뉴 연관관계 매핑
	// @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	// private List<Foodie> foodies = new ArrayList<>();

	//가게 장바구니 연관관계 매핑
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	private List<Cart> carts = new ArrayList<Cart>();

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	private List<StoreImg> imgs = new ArrayList<StoreImg>();

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	private List<Order> orders = new ArrayList<Order>();

	public void updateStatus(boolean status) {
		this.status = status;
	}

}
