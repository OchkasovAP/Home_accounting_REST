package ru.ochkasovap.homeAccountingRest.dto;

public class OperationFilter {
	private OperationDTO filter;
	private DateRange dateRange;
	public OperationDTO getFilter() {
		return filter;
	}
	public void setFilter(OperationDTO filter) {
		this.filter = filter;
	}
	public DateRange getDateRange() {
		return dateRange;
	}
	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
	
}
