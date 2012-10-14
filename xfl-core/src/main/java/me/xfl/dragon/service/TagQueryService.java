package me.xfl.dragon.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.model.Tag;
import me.xfl.dragon.repository.TagRepository;
import me.xfl.dragon.repository.impl.TagRepositoryImpl;
import me.xfl.jugg.Keys;
import me.xfl.jugg.repository.Query;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.service.ServiceException;
import me.xfl.jugg.util.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag query service.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Jun 28, 2012
 * @since 0.4.0
 */
public final class TagQueryService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(TagQueryService.class.getName());
	/**
	 * Tag repository.
	 */
	private TagRepository tagRepository = TagRepositoryImpl.getInstance();

	/**
	 * Gets a tag by the specified tag title.
	 * 
	 * @param tagTitle
	 *            the specified tag title
	 * @return for example,
	 * 
	 *         <pre>
	 * {
	 *     "tag": {
	 *         "oId": "",
	 *         "tagTitle": "",
	 *         "tagReferenceCount": int,
	 *         "tagPublishedRefCount": int
	 *     }
	 * }
	 * </pre>
	 * 
	 *         , returns {@code null} if not found
	 * @throws ServiceException
	 *             service exception
	 */
	public JSONObject getTagByTitle(final String tagTitle) throws ServiceException {
		try {
			final JSONObject ret = new JSONObject();

			final JSONObject tag = tagRepository.getByTitle(tagTitle);

			if (null == tag) {
				return null;
			}

			ret.put(Tag.TAG, tag);

			LOGGER.log(Level.FINER, "Got an tag[title={0}]", tagTitle);

			return ret;
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Gets an article failed", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * Gets the count of tags.
	 * 
	 * @return count of tags
	 * @throws ServiceException
	 *             service exception
	 */
	public long getTagCount() throws ServiceException {
		try {
			return tagRepository.count();
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Gets tags failed", e);

			throw new ServiceException(e);
		}
	}

	/**
	 * Gets all tags.
	 * 
	 * @return for example,
	 * 
	 *         <pre>
	 * [
	 *     {"tagTitle": "", "tagReferenceCount": int, ....},
	 *     ....
	 * ]
	 * </pre>
	 * 
	 *         , returns an empty list if not found
	 * @throws ServiceException
	 *             service exception
	 */
	public List<JSONObject> getTags() throws ServiceException {
		try {
			final Query query = new Query().setPageCount(1);

			final JSONObject result = tagRepository.get(query);
			final JSONArray tagArray = result.optJSONArray(Keys.RESULTS);

			return CollectionUtils.jsonArrayToList(tagArray);
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Gets tags failed", e);

			throw new ServiceException(e);
		}
	}

	/**
	 * Gets the {@link TagQueryService} singleton.
	 * 
	 * @return the singleton
	 */
	public static TagQueryService getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private constructor.
	 */
	private TagQueryService() {
	}

	/**
	 * Singleton holder.
	 * 
	 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
	 * @version 1.0.0.0, Oct 24, 2011
	 */
	private static final class SingletonHolder {

		/**
		 * Singleton.
		 */
		private static final TagQueryService SINGLETON = new TagQueryService();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
