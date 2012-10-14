package me.xfl.dragon.repository.impl;

import java.util.List;
import java.util.logging.Logger;

import me.xfl.dragon.model.Page;
import me.xfl.dragon.repository.PageRepository;
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
 * Page repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Dec 31, 2011
 * @since 0.3.1
 */
public final class PageRepositoryImpl extends AbstractRepository implements PageRepository {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(PageRepositoryImpl.class.getName());
	/**
	 * Singleton.
	 */
	private static final PageRepositoryImpl SINGLETON = new PageRepositoryImpl(Page.PAGE);

	@Override
	public JSONObject getByPermalink(final String permalink) throws RepositoryException {
		final Query query = new Query().setFilter(new PropertyFilter(Page.PAGE_PERMALINK, FilterOperator.EQUAL, permalink)).setPageCount(1);
		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	@Override
	public int getMaxOrder() throws RepositoryException {
		final Query query = new Query().addSort(Page.PAGE_ORDER, SortDirection.DESCENDING).setPageCount(1);
		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return -1;
		}

		return array.optJSONObject(0).optInt(Page.PAGE_ORDER);
	}

	@Override
	public JSONObject getUpper(final String id) throws RepositoryException {
		final JSONObject page = get(id);
		if (null == page) {
			return null;
		}

		final Query query = new Query()
				.setFilter(new PropertyFilter(Page.PAGE_ORDER, FilterOperator.LESS_THAN, page.optInt(Page.PAGE_ORDER)))
				.addSort(Page.PAGE_ORDER, SortDirection.DESCENDING).setCurrentPageNum(1).setPageSize(1).setPageCount(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (1 != array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	@Override
	public JSONObject getUnder(final String id) throws RepositoryException {
		final JSONObject page = get(id);
		if (null == page) {
			return null;
		}

		final Query query = new Query()
				.setFilter(new PropertyFilter(Page.PAGE_ORDER, FilterOperator.GREATER_THAN, page.optInt(Page.PAGE_ORDER)))
				.addSort(Page.PAGE_ORDER, SortDirection.ASCENDING).setCurrentPageNum(1).setPageSize(1).setPageCount(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (1 != array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	@Override
	public JSONObject getByOrder(final int order) throws RepositoryException {
		final Query query = new Query().setFilter(new PropertyFilter(Page.PAGE_ORDER, FilterOperator.EQUAL, order)).setPageCount(1);
		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	@Override
	public List<JSONObject> getPages() throws RepositoryException {
		final Query query = new Query().addSort(Page.PAGE_ORDER, SortDirection.ASCENDING).setPageCount(1);
		final JSONObject result = get(query);

		return CollectionUtils.jsonArrayToList(result.optJSONArray(Keys.RESULTS));
	}

	/**
	 * Gets the {@link PageRepositoryImpl} singleton.
	 * 
	 * @return the singleton
	 */
	public static PageRepositoryImpl getInstance() {
		return SINGLETON;
	}

	/**
	 * Private constructor.
	 * 
	 * @param name
	 *            the specified name
	 */
	private PageRepositoryImpl(final String name) {
		super(name);
	}
}
