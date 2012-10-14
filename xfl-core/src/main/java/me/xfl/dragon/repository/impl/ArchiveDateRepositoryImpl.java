package me.xfl.dragon.repository.impl;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.model.ArchiveDate;
import me.xfl.dragon.repository.ArchiveDateRepository;
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
 * Archive date repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Dec 31, 2011
 * @since 0.3.1
 */
public final class ArchiveDateRepositoryImpl extends AbstractRepository implements ArchiveDateRepository {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(ArchiveDateRepositoryImpl.class.getName());
	/**
	 * Singleton.
	 */
	private static final ArchiveDateRepositoryImpl SINGLETON = new ArchiveDateRepositoryImpl(ArchiveDate.ARCHIVE_DATE);

	@Override
	public JSONObject getByArchiveDate(final String archiveDate) throws RepositoryException {
		long time = 0L;
		try {
			time = ArchiveDate.DATE_FORMAT.parse(archiveDate).getTime();
		} catch (final ParseException e) {
			LOGGER.log(Level.SEVERE, "Can not parse archive date [" + archiveDate + "]", e);
			throw new RepositoryException("Can not parse archive date [" + archiveDate + "]");
		}

		final Query query = new Query();
		query.setFilter(new PropertyFilter(ArchiveDate.ARCHIVE_TIME, FilterOperator.EQUAL, time)).setPageCount(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	@Override
	public List<JSONObject> getArchiveDates() throws RepositoryException {
		final Query query = new Query().addSort(ArchiveDate.ARCHIVE_TIME, SortDirection.DESCENDING)
				.setPageCount(1);
		final JSONObject result = get(query);

		final JSONArray archiveDates = result.optJSONArray(Keys.RESULTS);
		final List<JSONObject> ret = CollectionUtils.jsonArrayToList(archiveDates);

		removeForUnpublishedArticles(ret);

		return ret;
	}

	/**
	 * Removes archive dates of unpublished articles from the specified archive
	 * dates.
	 * 
	 * @param archiveDates
	 *            the specified archive dates
	 * @throws RepositoryException
	 *             repository exception
	 */
	private void removeForUnpublishedArticles(final List<JSONObject> archiveDates) throws RepositoryException {
		final Iterator<JSONObject> iterator = archiveDates.iterator();
		while (iterator.hasNext()) {
			final JSONObject archiveDate = iterator.next();
			if (0 == archiveDate.optInt(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT)) {
				iterator.remove();
			}
		}
	}

	/**
	 * Gets the {@link ArchiveDateRepositoryImpl} singleton.
	 * 
	 * @return the singleton
	 */
	public static ArchiveDateRepositoryImpl getInstance() {
		return SINGLETON;
	}

	/**
	 * Private constructor.
	 * 
	 * @param name
	 *            the specified name
	 */
	private ArchiveDateRepositoryImpl(final String name) {
		super(name);
	}
}
