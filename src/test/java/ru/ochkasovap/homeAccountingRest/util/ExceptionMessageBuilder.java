package ru.ochkasovap.homeAccountingRest.util;

import java.util.List;
import java.util.Map;

public class ExceptionMessageBuilder {
	public static String build(Map<String, String> fieldErrors) {
		StringBuilder stringBuilder = new StringBuilder();
		fieldErrors.keySet().stream().forEach(field ->stringBuilder.append("Field - ")
																	.append(field)
																	.append(", error - ")
																	.append(fieldErrors.get(field))
																	.append(";"));
		return stringBuilder.toString();
	}
}
