package ru.ochkasovap.homeAccountingRest.dto;

import ru.ochkasovap.homeAccountingRest.util.DateRange;

public class OperationFilterDTO {
	
	private String account="";
	private String category="";
	private DateRange dateRange = DateRange.defaultDateRange();

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public DateRange getDateRange() {
		return dateRange;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
}
