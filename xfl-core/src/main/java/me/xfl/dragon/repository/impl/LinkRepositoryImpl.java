package me.xfl.dragon.repository.impl;

import java.util.logging.Logger;

import me.xfl.dragon.model.Link;
import me.xfl.dragon.repository.LinkRepository;
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
 * Link repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Nov 10, 2011
 * @since 0.3.1
 */
public final class LinkRepositoryImpl extends AbstractRepository implements LinkRepository {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(LinkRepositoryImpl.class.getName());
	/**
	 * Singleton.
	 */
	private static final LinkRepositoryImpl SINGLETON = new LinkRepositoryImpl(Link.LINK);

	@Override
	public JSONObject getByAddress(final String address) throws RepositoryException {
		final Query query = new Query().setFilter(new PropertyFilter(Link.LINK_ADDRESS, FilterOperator.EQUAL, address)).setPageCount(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	@Override
	public int getMaxOrder() throws RepositoryException {
		final Query query = new Query();
		query.addSort(Link.LINK_ORDER, SortDirection.DESCENDING);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return -1;
		}

		return array.optJSONObject(0).optInt(Link.LINK_ORDER);
	}

	@Override
	public JSONObject getByOrder(final int order) throws RepositoryException {
		final Query query = new Query();
		query.setFilter(new PropertyFilter(Link.LINK_ORDER, FilterOperator.EQUAL, order));

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	@Override
	public JSONObject getUpper(final String id) throws RepositoryException {
		final JSONObject link = get(id);
		if (null == link) {
			return null;
		}

		final Query query = new Query();
		query.setFilter(new PropertyFilter(Link.LINK_ORDER, FilterOperator.LESS_THAN, link.optInt(Link.LINK_ORDER))).addSort(
				Link.LINK_ORDER, SortDirection.DESCENDING);
		query.setCurrentPageNum(1);
		query.setPageSize(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (1 != array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	@Override
	public JSONObject getUnder(final String id) throws RepositoryException {
		final JSONObject link = get(id);
		if (null == link) {
			return null;
		}

		final Query query = new Query();
		query.setFilter(new PropertyFilter(Link.LINK_ORDER, FilterOperator.GREATER_THAN, link.optInt(Link.LINK_ORDER))).addSort(
				Link.LINK_ORDER, SortDirection.ASCENDING);
		query.setCurrentPageNum(1);
		query.setPageSize(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (1 != array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	/**
	 * Gets the {@link LinkRepositoryImpl} singleton.
	 * 
	 * @return the singleton
	 */
	public static LinkRepositoryImpl getInstance() {
		return SINGLETON;
	}

	/**
	 * Private constructor.
	 * 
	 * @param name
	 *            the specified name
	 */
	private LinkRepositoryImpl(final String name) {
		super(name);
	}
}
