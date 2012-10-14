package me.xfl.jugg.util;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Date utilities.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Jun 19, 2011
 */
public final class Dates {

	/**
	 * English month.
	 */
	public static final Map<String, String> EN_MONTHS = new HashMap<String, String>();

	static {
		EN_MONTHS.put("01", "January");
		EN_MONTHS.put("02", "February");
		EN_MONTHS.put("03", "March");
		EN_MONTHS.put("04", "April");
		EN_MONTHS.put("05", "May");
		EN_MONTHS.put("06", "June");
		EN_MONTHS.put("07", "Jule");
		EN_MONTHS.put("08", "August");
		EN_MONTHS.put("09", "September");
		EN_MONTHS.put("10", "October");
		EN_MONTHS.put("11", "November");
		EN_MONTHS.put("12", "December");
	}

	/**
	 * Private default constructor.
	 */
	private Dates() {
	}

	/**
	 * Gets current date time string with the specified date formatter.
	 * 
	 * @param dateFormat
	 *            the specified date formatter
	 * @return a date time string
	 */
	public static String currentDatetime(final DateFormat dateFormat) {
		return dateFormat.format(System.currentTimeMillis());
	}
}
