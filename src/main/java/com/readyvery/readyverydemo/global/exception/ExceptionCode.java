package com.readyvery.readyverydemo.global.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
	STORE_NOT_FOUND(404, "Store does not exists."),
	USER_NOT_FOUND(404, "User does not exists."),
	FOODY_NOT_FOUND(404, "Foody does not exists."),
	FOODY_NOT_IN_STORE(400, "Foody does not exists in store."),
	INVALID_OPTION_COUNT(400, "Invalid option count."),
	INVALID_OPTION(400, "Invalid option."),
	OPTION_NOT_FOUND(404, "Option does not exists."),
	CART_ITEM_NOT_FOUND(404, "Cart item does not exists."),
	NOT_FOUND_STORE(404, "Not found store."),
	NOT_LOGIN_USER(401, "Not login user."),
	NOT_FOUND_ORDER(404, "Not found order."),
	NOT_PROGRESS_ORDER(400, "Not progress order."),
	NOT_CHANGE_ORDER(400, "Not change order."),
	NOT_FOUND_TIME(404, "Not found time."),
	TOSS_PAYMENT_SUCCESS_FAIL(400, "Toss payment success fail."),
	NOT_FOUND_REJECT_REASON(404, "Not found reject reason."),
	FOODIE_NOT_FOUND(404, "Foodie does not exists."),
	FOODIE_NOT_MATCHED_STORE(400, "Foodie does not match in store."),
	SALE_NOT_FOUND(404, "Sale does not exists."),
	EMAIL_DUPLICATION(404, "Email is duplicated."),
	PHONE_NUMBER_DUPLICATION(404, "Phone number is duplicated."),
	INVALID_INPUT(400, "Invalid input."),
	AUTH_ERROR(401, "Auth Error"),
	NOT_REJECT_ROLE(403, "Not reject role."),
	BAD_REQUEST(400, "Bad request."),
	NOT_EQUAL_PARAMETER(400, "Not equal parameter");

	private int status;
	private String message;

	ExceptionCode(int status, String message) {
		this.status = status;
		this.message = message;
	}
}
