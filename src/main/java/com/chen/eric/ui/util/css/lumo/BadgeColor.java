package com.chen.eric.ui.util.css.lumo;

public enum BadgeColor {

	NORMAL("badge"), NORMAL_PRIMARY("badge primary"), SUCCESS(
			"badge success"), SUCCESS_PRIMARY("badge success primary"), ERROR(
			"badge error"), ERROR_PRIMARY(
			"badge error primary"), CONTRAST(
			"badge contrast"), CONTRAST_PRIMARY(
			"badge contrast primary");

	private String style;

	BadgeColor(String style) {
		this.style = style;
	}

	public String getThemeName() {
		return style;
	}
	
	public static BadgeColor getThemeByStatus(String status) {
		if (status.equals("Complete")) {
			return SUCCESS;
		} else if (status.equals("Pending")) {
			return ERROR;
		} else if (status.equals("Incomplete")) {
			return NORMAL;
		}
		return CONTRAST;
	}

}
