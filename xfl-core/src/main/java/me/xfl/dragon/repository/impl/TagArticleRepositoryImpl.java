package me.xfl.dragon.repository.impl;

import java.util.List;
import java.util.logging.Logger;

import me.xfl.dragon.model.Article;
import me.xfl.dragon.model.Tag;
import me.xfl.dragon.repository.TagArticleRepository;
import me.xfl.jugg.Keys;
import me.xfl.jugg.repository.AbstractRepository;
import me.xfl.jugg.repository.FilterOperator;
import me.xfl.jugg.repository.PropertyFilter;
import me.xfl.jugg.repository.Query;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.repository.SortDirection;
import me.xfl.jugg.util.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag-Article relation repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Nov 9, 2011
 * @since 0.3.1
 */
public final class TagArticleRepositoryImpl extends AbstractRepository implements TagArticleRepository {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(TagArticleRepositoryImpl.class.getName());
	/**
	 * Singleton.
	 */
	private static final TagArticleRepositoryImpl SINGLETON = new TagArticleRepositoryImpl(Tag.TAG + "_" + Article.ARTICLE);

	@Override
	public List<JSONObject> getByArticleId(final String articleId) throws RepositoryException {
		final Query query = new Query().setFilter(
				new PropertyFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, articleId)).setPageCount(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		return CollectionUtils.jsonArrayToList(array);
	}

	@Override
	public JSONObject getByTagId(final String tagId, final int currentPageNum, final int pageSize) throws RepositoryException {
		final Query query = new Query().setFilter(new PropertyFilter(Tag.TAG + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, tagId))
				.addSort(Article.ARTICLE + "_" + Keys.OBJECT_ID, SortDirection.DESCENDING).setCurrentPageNum(currentPageNum)
				.setPageSize(pageSize).setPageCount(1);

		return get(query);
	}

	/**
	 * Gets the {@link TagArticleRepositoryImpl} singleton.
	 * 
	 * @return the singleton
	 */
	public static TagArticleRepositoryImpl getInstance() {
		return SINGLETON;
	}

	/**
	 * Private constructor.
	 * 
	 * @param name
	 *            the specified name
	 */
	private TagArticleRepositoryImpl(final String name) {
		super(name);
	}
}
