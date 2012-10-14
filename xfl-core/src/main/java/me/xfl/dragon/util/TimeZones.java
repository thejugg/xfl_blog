package me.xfl.dragon.util;

import java.util.Date;
import java.util.TimeZone;

import me.xfl.dragon.model.ArchiveDate;
import me.xfl.dragon.model.Comment;
import me.xfl.jugg.util.freemarker.Templates;

/**
 * Time zone utilities.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Dec 29, 2011
 */
public final class TimeZones {

	/**
	 * Gets the current date with the specified time zone id.
	 * 
	 * @param timeZoneId
	 *            the specified time zone id
	 * @return date
	 */
	public static Date getTime(final String timeZoneId) {
		final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
		final TimeZone defaultTimeZone = TimeZone.getDefault();
		TimeZone.setDefault(timeZone);
		final Date ret = new Date();
		TimeZone.setDefault(defaultTimeZone);

		return ret;
	}

	/**
	 * Sets time zone by the specified time zone id.
	 * 
	 * <p>
	 * This method will call
	 * {@linkplain TimeZone#setDefault(java.util.TimeZone)}, and set time zone
	 * for all date formats and template configuration.
	 * </p>
	 * 
	 * @param timeZoneId
	 *            the specified time zone id
	 */
	public static void setTimeZone(final String timeZoneId) {
		final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

		TimeZone.setDefault(timeZone);
		System.setProperty("user.timezone", timeZoneId);
		ArchiveDate.DATE_FORMAT.setTimeZone(timeZone);
		Comment.DATE_FORMAT.setTimeZone(timeZone);
		Templates.MAIN_CFG.setTimeZone(timeZone);
		Templates.MOBILE_CFG.setTimeZone(timeZone);
	}

	/**
	 * Private default constructor.
	 */
	private TimeZones() {
	}
}
