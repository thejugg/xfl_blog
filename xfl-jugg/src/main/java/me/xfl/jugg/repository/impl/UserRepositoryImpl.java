package me.xfl.jugg.repository.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.jugg.Keys;
import me.xfl.jugg.model.Role;
import me.xfl.jugg.model.User;
import me.xfl.jugg.repository.AbstractRepository;
import me.xfl.jugg.repository.FilterOperator;
import me.xfl.jugg.repository.PropertyFilter;
import me.xfl.jugg.repository.Query;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User repository implementation.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 27, 2011
 */
public final class UserRepositoryImpl extends AbstractRepository {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(UserRepositoryImpl.class.getName());

	/**
	 * Gets user by the specified email.
	 * 
	 * @param email
	 *            the specified email
	 * @return user, returns {@code null} if not found
	 */
	public JSONObject getByEmail(final String email) {
		final Query query = new Query();
		query.setFilter(new PropertyFilter(User.USER_EMAIL, FilterOperator.EQUAL, email.toLowerCase().trim()));

		try {
			final JSONObject result = get(query);
			final JSONArray array = result.getJSONArray(Keys.RESULTS);

			if (0 == array.length()) {
				return null;
			}

			return array.getJSONObject(0);
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

			return null;
		}
	}

	/**
	 * Gets the administrator.
	 * 
	 * @return administrator, returns {@code null} if not found
	 */
	public JSONObject getAdmin() {
		final Query query = new Query();
		query.setFilter(new PropertyFilter(User.USER_ROLE, FilterOperator.EQUAL, Role.ADMIN_ROLE));
		try {
			final JSONObject result = get(query);
			final JSONArray array = result.getJSONArray(Keys.RESULTS);

			if (0 == array.length()) {
				return null;
			}

			return array.getJSONObject(0);
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

			return null;
		}
	}

	/**
	 * Determines the specified email is administrator's or not.
	 * 
	 * @param email
	 *            the specified email
	 * @return {@code true} if it is, returns {@code false} otherwise
	 * @throws RepositoryException
	 *             repository exception
	 */
	public boolean isAdminEmail(final String email) throws RepositoryException {
		final JSONObject user = getByEmail(email);

		if (null == user) {
			return false;
		}

		try {
			return Role.ADMIN_ROLE.equals(user.getString(User.USER_ROLE));
		} catch (final JSONException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

			throw new RepositoryException(e);
		}
	}

	/**
	 * Gets the {@link UserRepositoryImpl} singleton.
	 * 
	 * @return the singleton
	 */
	public static UserRepositoryImpl getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private constructor.
	 * 
	 * @param name
	 *            the specified name
	 */
	private UserRepositoryImpl(final String name) {
		super(name);
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
		private static final UserRepositoryImpl SINGLETON = new UserRepositoryImpl(User.USER);

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
