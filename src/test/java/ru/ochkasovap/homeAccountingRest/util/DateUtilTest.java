package ru.ochkasovap.homeAccountingRest.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;

class DateUtilTest {
	@Test
	void convertStringToDate() {
		Date expected = new GregorianCalendar(2023, 0, 1).getTime();
		assertEquals(expected, DateUtil.convertStringToDate("2023-01-01"));
	}
	@Test
	void convertStringToDate_UncorrectDate() {
		Calendar expected = Calendar.getInstance();
		Calendar actual = Calendar.getInstance();
		actual.setTime(DateUtil.convertStringToDate("2023,01,01"));
		assertEquals(expected.get(Calendar.YEAR), actual.get(Calendar.YEAR));
		assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH));
		assertEquals(expected.get(Calendar.DAY_OF_MONTH), actual.get(Calendar.DAY_OF_MONTH));
	}
	@Test
	void convertDateToString() {
		String expected = "2023-01-01";
		String actual = DateUtil.convertDateToString(new GregorianCalendar(2023,0,01).getTime());
		assertEquals(expected, actual);
	}
}
