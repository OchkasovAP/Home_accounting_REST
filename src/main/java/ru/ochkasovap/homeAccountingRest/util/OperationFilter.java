package ru.ochkasovap.homeAccountingRest.util;

public class OperationFilter {
	private String account="";
	private String category="";
	
	private OperationType type;
	
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
	public OperationType getType() {
		return type;
	}
	public void setType(OperationType type) {
		this.type = type;
	}
	public DateRange getDateRange() {
		return dateRange;
	}
	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
	
}
