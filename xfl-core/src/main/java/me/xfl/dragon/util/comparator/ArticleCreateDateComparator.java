package me.xfl.dragon.util.comparator;

import java.util.Comparator;
import java.util.Date;

import me.xfl.dragon.model.Article;

import org.json.JSONObject;

/**
 * Article comparator by create date.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Dec 30, 2010
 */
public final class ArticleCreateDateComparator implements Comparator<JSONObject> {

	/**
	 * Package default constructor.
	 */
	ArticleCreateDateComparator() {
	}

	@Override
	public int compare(final JSONObject article1, final JSONObject article2) {
		try {
			final Date date1 = (Date) article1.get(Article.ARTICLE_CREATE_DATE);
			final Date date2 = (Date) article2.get(Article.ARTICLE_CREATE_DATE);

			return date2.compareTo(date1);
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
