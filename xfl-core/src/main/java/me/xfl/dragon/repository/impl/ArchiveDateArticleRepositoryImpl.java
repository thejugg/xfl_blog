package me.xfl.dragon.repository.impl;

import me.xfl.dragon.model.ArchiveDate;
import me.xfl.dragon.model.Article;
import me.xfl.dragon.repository.ArchiveDateArticleRepository;
import me.xfl.jugg.Keys;
import me.xfl.jugg.repository.AbstractRepository;
import me.xfl.jugg.repository.FilterOperator;
import me.xfl.jugg.repository.PropertyFilter;
import me.xfl.jugg.repository.Query;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.repository.SortDirection;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Archive date-Article relation repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Nov 9, 2011
 * @since 0.3.1
 */
public final class ArchiveDateArticleRepositoryImpl extends AbstractRepository implements ArchiveDateArticleRepository {

	/**
	 * Singleton.
	 */
	private static final ArchiveDateArticleRepositoryImpl SINGLETON = new ArchiveDateArticleRepositoryImpl(ArchiveDate.ARCHIVE_DATE + "_"
			+ Article.ARTICLE);

	@Override
	public JSONObject getByArchiveDateId(final String archiveDateId, final int currentPageNum, final int pageSize)
			throws RepositoryException {
		final Query query = new Query()
				.setFilter(new PropertyFilter(ArchiveDate.ARCHIVE_DATE + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, archiveDateId))
				.addSort(Article.ARTICLE + "_" + Keys.OBJECT_ID, SortDirection.DESCENDING).setCurrentPageNum(currentPageNum)
				.setPageSize(pageSize).setPageCount(1);

		return get(query);
	}

	@Override
	public JSONObject getByArticleId(final String articleId) throws RepositoryException {
		final Query query = new Query();
		query.setFilter(new PropertyFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, articleId));

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);
		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	/**
	 * Gets the {@link ArchiveDateArticleRepositoryImpl} singleton.
	 * 
	 * @return the singleton
	 */
	public static ArchiveDateArticleRepositoryImpl getInstance() {
		return SINGLETON;
	}

	/**
	 * Private constructor.
	 * 
	 * @param name
	 *            the specified name
	 */
	private ArchiveDateArticleRepositoryImpl(final String name) {
		super(name);
	}
}
