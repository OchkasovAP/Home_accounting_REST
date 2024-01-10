package ru.ochkasovap.homeAccountingRest.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
	public static Date convertStringToDate(String dateParam) {
		Date date = new Date();
		if (correctDateParam(dateParam)) {
			String[] dateParts = dateParam.split("-");
			int year = Integer.parseInt(dateParts[0]);
			int month = Integer.parseInt(dateParts[1]) - 1;
			int day = Integer.parseInt(dateParts[2]);
			Date dateFromParam = new GregorianCalendar(year, month, day).getTime();
			if (dateFromParam.getTime() < date.getTime()) {
				date = dateFromParam;
			}
		}
		return date;
	}

	public static String convertDateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}

	private static boolean correctDateParam(String dateParam) {
		if (dateParam != null) {
			return dateParam.matches("\\d{4}-\\d{2}-\\d{2}");
		}
		return false;
	}
}
