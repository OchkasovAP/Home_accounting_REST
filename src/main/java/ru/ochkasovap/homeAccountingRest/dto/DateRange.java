package ru.ochkasovap.homeAccountingRest.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.servlet.http.HttpServletRequest;
import ru.ochkasovap.homeAccountingRest.util.DateUtil;
@Component
public class DateRange {
	private Date startDate;
	private Date endDate;
	
	public DateRange() {
		
	}

	public DateRange(Date startDate, Date endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public DateRange(String startDate, String endDate) {
		this.startDate = DateUtil.convertStringToDate(startDate);
		this.endDate = DateUtil.convertStringToDate(endDate);
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	@Override
	public String toString() {
		return "Период: " + DateUtil.convertDateToString(startDate) + " : " + DateUtil.convertDateToString(endDate);
	}

}
