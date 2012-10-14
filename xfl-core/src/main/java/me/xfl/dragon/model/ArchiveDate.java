package me.xfl.dragon.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * This class defines all archive date model relevant keys.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Jul 2, 2011
 */
public final class ArchiveDate {

	/**
	 * Archive date.
	 */
	public static final String ARCHIVE_DATE = "archiveDate";
	/**
	 * Archive dates.
	 */
	public static final String ARCHIVE_DATES = "archiveDates";
	/**
	 * Archive time.
	 */
	public static final String ARCHIVE_TIME = "archiveTime";
	/**
	 * Key of archive date article count.
	 */
	public static final String ARCHIVE_DATE_ARTICLE_COUNT = "archiveDateArticleCount";
	/**
	 * Key of archive date article count.
	 */
	public static final String ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT = "archiveDatePublishedArticleCount";
	/**
	 * Archive date year.
	 */
	public static final String ARCHIVE_DATE_YEAR = "archiveDateYear";
	/**
	 * Archive date month.
	 */
	public static final String ARCHIVE_DATE_MONTH = "archiveDateMonth";
	/**
	 * Date format(yyyy/MM).
	 */
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM");

	/**
	 * Private default constructor.
	 */
	private ArchiveDate() {
	}
}
