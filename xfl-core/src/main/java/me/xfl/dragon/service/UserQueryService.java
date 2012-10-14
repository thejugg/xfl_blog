package me.xfl.dragon.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.repository.UserRepository;
import me.xfl.dragon.repository.impl.UserRepositoryImpl;
import me.xfl.jugg.Keys;
import me.xfl.jugg.model.Pagination;
import me.xfl.jugg.model.User;
import me.xfl.jugg.repository.Query;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.service.ServiceException;
import me.xfl.jugg.user.UserService;
import me.xfl.jugg.user.UserServiceFactory;
import me.xfl.jugg.util.Paginator;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * User query service.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Feb 24, 2012
 * @since 0.4.0
 */
public final class UserQueryService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(UserQueryService.class.getName());
	/**
	 * User service.
	 */
	private UserService userService = UserServiceFactory.getUserService();
	/**
	 * User repository.
	 */
	private UserRepository userRepository = UserRepositoryImpl.getInstance();

	/**
	 * Gets the administrator.
	 * 
	 * @return administrator, returns {@code null} if not found
	 * @throws ServiceException
	 *             service exception
	 */
	public JSONObject getAdmin() throws ServiceException {
		try {
			return userRepository.getAdmin();
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Gets admin failed", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * Gets a user by the specified email.
	 * 
	 * @param email
	 *            the specified email
	 * @return user, returns {@code null} if not found
	 * @throws ServiceException
	 *             service exception
	 */
	public JSONObject getUserByEmail(final String email) throws ServiceException {
		try {
			return userRepository.getByEmail(email);
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Gets user by email[" + email + "] failed", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * Gets users by the specified request json object.
	 * 
	 * @param requestJSONObject
	 *            the specified request json object, for example,
	 * 
	 *            <pre>
	 * {
	 *     "paginationCurrentPageNum": 1,
	 *     "paginationPageSize": 20,
	 *     "paginationWindowSize": 10,
	 * }, see {@link Pagination} for more details
	 * </pre>
	 * @return for example,
	 * 
	 *         <pre>
	 * {
	 *     "pagination": {
	 *         "paginationPageCount": 100,
	 *         "paginationPageNums": [1, 2, 3, 4, 5]
	 *     },
	 *     "users": [{
	 *         "oId": "",
	 *         "userName": "",
	 *         "userEmail": "",
	 *         "userPassword": "",
	 *         "roleName": ""
	 *      }, ....]
	 * }
	 * </pre>
	 * @throws ServiceException
	 *             service exception
	 * @see Pagination
	 */
	public JSONObject getUsers(final JSONObject requestJSONObject) throws ServiceException {
		final JSONObject ret = new JSONObject();

		final int currentPageNum = requestJSONObject.optInt(Pagination.PAGINATION_CURRENT_PAGE_NUM);
		final int pageSize = requestJSONObject.optInt(Pagination.PAGINATION_PAGE_SIZE);
		final int windowSize = requestJSONObject.optInt(Pagination.PAGINATION_WINDOW_SIZE);
		final Query query = new Query().setCurrentPageNum(currentPageNum).setPageSize(pageSize);

		JSONObject result = null;

		try {
			result = userRepository.get(query);
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Gets users failed", e);

			throw new ServiceException(e);
		}

		final int pageCount = result.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);

		final JSONObject pagination = new JSONObject();
		ret.put(Pagination.PAGINATION, pagination);
		final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);
		pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
		pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

		final JSONArray users = result.optJSONArray(Keys.RESULTS);
		ret.put(User.USERS, users);

		return ret;
	}

	/**
	 * Gets a user by the specified user id.
	 * 
	 * @param userId
	 *            the specified user id
	 * @return for example,
	 * 
	 *         <pre>
	 * {
	 *     "user": {
	 *         "oId": "",
	 *         "userName": "",
	 *         "userEmail": "",
	 *         "userPassword": ""
	 *     }
	 * }
	 * </pre>
	 * 
	 *         , returns {@code null} if not found
	 * @throws ServiceException
	 *             service exception
	 */
	public JSONObject getUser(final String userId) throws ServiceException {
		final JSONObject ret = new JSONObject();

		JSONObject user = null;
		try {
			user = userRepository.get(userId);
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Gets a user failed", e);
			throw new ServiceException(e);
		}

		if (null == user) {
			return null;
		}

		ret.put(User.USER, user);

		return ret;
	}

	/**
	 * Gets the URL of user logout.
	 * 
	 * @return logout URL, returns {@code null} if the user is not logged in
	 */
	public String getLogoutURL() {
		return userService.createLogoutURL("/");
	}

	/**
	 * Gets the URL of user login.
	 * 
	 * @param redirectURL
	 *            redirect URL after logged in
	 * @return login URL
	 */
	public String getLoginURL(final String redirectURL) {
		return userService.createLoginURL(redirectURL);
	}

	/**
	 * Gets the {@link UserQueryService} singleton.
	 * 
	 * @return the singleton
	 */
	public static UserQueryService getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private constructor.
	 */
	private UserQueryService() {
	}

	/**
	 * Singleton holder.
	 * 
	 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
	 * @version 1.0.0.0, Oct 28, 2011
	 */
	private static final class SingletonHolder {

		/**
		 * Singleton.
		 */
		private static final UserQueryService SINGLETON = new UserQueryService();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
