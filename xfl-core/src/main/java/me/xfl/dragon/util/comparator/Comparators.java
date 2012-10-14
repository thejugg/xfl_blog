package me.xfl.dragon.util.comparator;

/**
 * Comparators utilities.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Dec 30, 2010
 */
public final class Comparators {

	/**
	 * Article create date comparator.
	 */
	public static final ArticleCreateDateComparator ARTICLE_CREATE_DATE_COMPARATOR = new ArticleCreateDateComparator();
	/**
	 * Article update date comparator.
	 */
	public static final ArticleUpdateDateComparator ARTICLE_UPDATE_DATE_COMPARATOR = new ArticleUpdateDateComparator();
	/**
	 * Tag reference count comparator.
	 */
	public static final TagRefCntComparator TAG_REF_CNT_COMPARATOR = new TagRefCntComparator();

	/**
	 * Private default constructor.
	 */
	private Comparators() {
	}
}
