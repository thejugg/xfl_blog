package me.xfl.dragon.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.model.Preference;
import me.xfl.dragon.repository.PreferenceRepository;
import me.xfl.dragon.repository.impl.PreferenceRepositoryImpl;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.service.ServiceException;

import org.json.JSONObject;

/**
 * Preference query service.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 31, 2011
 * @since 0.4.0
 */
public final class PreferenceQueryService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(PreferenceQueryService.class.getName());
	/**
	 * Preference repository.
	 */
	private PreferenceRepository preferenceRepository = PreferenceRepositoryImpl.getInstance();

	/**
	 * Gets the reply notification template.
	 * 
	 * @return reply notification template, returns {@code null} if not found
	 * @throws ServiceException
	 *             service exception
	 */
	public JSONObject getReplyNotificationTemplate() throws ServiceException {
		try {
			return preferenceRepository.get(Preference.REPLY_NOTIFICATION_TEMPLATE);
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Updates reply notification template failed", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * Gets the user preference.
	 * 
	 * <p>
	 * <b>Note</b>: Invoking the method will not load skin.
	 * </p>
	 * 
	 * @return user preference, returns {@code null} if not found
	 * @throws ServiceException
	 *             if repository exception
	 */
	public JSONObject getPreference() throws ServiceException {
		try {
			final JSONObject ret = preferenceRepository.get(Preference.PREFERENCE);
			if (null == ret) {
				LOGGER.log(Level.WARNING, "Can not load preference from datastore");
				return null;
			}

			return ret;
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Gets the {@link PreferenceQueryService} singleton.
	 * 
	 * @return the singleton
	 */
	public static PreferenceQueryService getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private constructor.
	 */
	private PreferenceQueryService() {
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
		private static final PreferenceQueryService SINGLETON = new PreferenceQueryService();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
