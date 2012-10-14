package me.xfl.dragon.util;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import me.xfl.dragon.model.Tag;
import me.xfl.dragon.repository.TagRepository;
import me.xfl.dragon.repository.impl.TagRepositoryImpl;
import me.xfl.jugg.Keys;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tag utilities.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Mar 8, 2011
 */
public final class Tags {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(Tags.class.getName());
	/**
	 * Tag repository.
	 */
	private TagRepository tagRepository = TagRepositoryImpl.getInstance();
	/**
	 * Article utilities.
	 */
	private Articles articleUtils = Articles.getInstance();

	/**
	 * Decrements reference count of every tag of an published article specified
	 * by the given article id.
	 * 
	 * @param articleId
	 *            the given article id
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void decTagPublishedRefCount(final String articleId) throws JSONException, RepositoryException {
		final List<JSONObject> tags = tagRepository.getByArticleId(articleId);

		for (final JSONObject tag : tags) {
			final String tagId = tag.getString(Keys.OBJECT_ID);
			final int refCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);
			tag.put(Tag.TAG_REFERENCE_COUNT, refCnt);
			final int publishedRefCnt = tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT);
			tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, publishedRefCnt - 1);
			tagRepository.update(tagId, tag);
		}
	}

	/**
	 * Removes tags of unpublished articles from the specified tags.
	 * 
	 * @param tags
	 *            the specified tags
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void removeForUnpublishedArticles(final List<JSONObject> tags) throws JSONException, RepositoryException {
		final Iterator<JSONObject> iterator = tags.iterator();
		while (iterator.hasNext()) {
			final JSONObject tag = iterator.next();
			if (0 == tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT)) {
				iterator.remove();
			}
		}
	}

	/**
	 * Gets the {@link Tags} singleton.
	 * 
	 * @return the singleton
	 */
	public static Tags getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private default constructor.
	 */
	private Tags() {
	}

	/**
	 * Singleton holder.
	 * 
	 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
	 * @version 1.0.0.0, Jan 12, 2011
	 */
	private static final class SingletonHolder {

		/**
		 * Singleton.
		 */
		private static final Tags SINGLETON = new Tags();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
