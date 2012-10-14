package me.xfl.dragon.repository.impl;

import java.util.logging.Logger;

import me.xfl.dragon.repository.UserRepository;
import me.xfl.jugg.Keys;
import me.xfl.jugg.model.Role;
import me.xfl.jugg.model.User;
import me.xfl.jugg.repository.AbstractRepository;
import me.xfl.jugg.repository.FilterOperator;
import me.xfl.jugg.repository.PropertyFilter;
import me.xfl.jugg.repository.Query;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * User repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Nov 10, 2011
 * @since 0.3.1
 */
public final class UserRepositoryImpl extends AbstractRepository implements UserRepository {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(UserRepositoryImpl.class.getName());
	/**
	 * Singleton.
	 */
	private static final UserRepositoryImpl SINGLETON = new UserRepositoryImpl(User.USER);

	@Override
	public JSONObject getByEmail(final String email) throws RepositoryException {
		final Query query = new Query().setPageCount(1);
		query.setFilter(new PropertyFilter(User.USER_EMAIL, FilterOperator.EQUAL, email.toLowerCase().trim()));

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	@Override
	public JSONObject getAdmin() throws RepositoryException {
		final Query query = new Query().setFilter(new PropertyFilter(User.USER_ROLE, FilterOperator.EQUAL, Role.ADMIN_ROLE))
				.setPageCount(1);
		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	@Override
	public boolean isAdminEmail(final String email) throws RepositoryException {
		final JSONObject user = getByEmail(email);

		if (null == user) {
			return false;
		}

		return Role.ADMIN_ROLE.equals(user.optString(User.USER_ROLE));
	}

	/**
	 * Gets the {@link UserRepositoryImpl} singleton.
	 * 
	 * @return the singleton
	 */
	public static UserRepositoryImpl getInstance() {
		return SINGLETON;
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
}
