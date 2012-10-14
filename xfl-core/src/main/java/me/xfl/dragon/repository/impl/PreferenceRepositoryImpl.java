package me.xfl.dragon.repository.impl;

import java.util.logging.Logger;

import me.xfl.dragon.model.Preference;
import me.xfl.dragon.repository.PreferenceRepository;
import me.xfl.jugg.repository.AbstractRepository;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONObject;

/**
 * Preference repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Feb 28, 2012
 * @since 0.3.1
 */
public final class PreferenceRepositoryImpl extends AbstractRepository implements PreferenceRepository {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(PreferenceRepositoryImpl.class.getName());
	/**
	 * Singleton.
	 */
	private static final PreferenceRepositoryImpl SINGLETON = new PreferenceRepositoryImpl(Preference.PREFERENCE);

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Bypasses {@linkplain org.b3log.latke.repository.Repositories validation}
	 * against the repository structure, adds the specified json object as
	 * preference directly.
	 * </p>
	 */
	@Override
	public String add(final JSONObject jsonObject) throws RepositoryException {
		return getUnderlyingRepository().add(jsonObject);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Bypasses {@linkplain org.b3log.latke.repository.Repositories validation}
	 * against the repository structure, adds the specified json object as
	 * preference directly.
	 * </p>
	 */
	@Override
	public void update(final String id, final JSONObject jsonObject) throws RepositoryException {
		getUnderlyingRepository().update(id, jsonObject);
	}

	/**
	 * Gets the {@link PreferenceRepositoryImpl} singleton.
	 * 
	 * @return the singleton
	 */
	public static PreferenceRepositoryImpl getInstance() {
		return SINGLETON;
	}

	/**
	 * Private constructor.
	 * 
	 * @param name
	 *            the specified name
	 */
	private PreferenceRepositoryImpl(final String name) {
		super(name);
	}
}
