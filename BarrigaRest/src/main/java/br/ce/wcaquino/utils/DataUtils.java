package br.ce.wcaquino.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataUtils {
	
	public static String getDataDiferencaDias(Integer qtdDias) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, qtdDias);
		return getDataFormatada(calendar.getTime());
		//formatar em string
	}
	
	public static String getDataFormatada(java.util.Date date) {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(date);
	}
}
