package me.xfl.dragon.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.xfl.dragon.model.Article;
import me.xfl.dragon.processor.LoginProcessor;
import me.xfl.dragon.repository.ArticleRepository;
import me.xfl.dragon.repository.UserRepository;
import me.xfl.dragon.repository.impl.ArticleRepositoryImpl;
import me.xfl.dragon.repository.impl.UserRepositoryImpl;
import me.xfl.jugg.Keys;
import me.xfl.jugg.model.User;
import me.xfl.jugg.repository.Query;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.service.ServiceException;
import me.xfl.jugg.user.GeneralUser;
import me.xfl.jugg.user.UserService;
import me.xfl.jugg.user.UserServiceFactory;
import me.xfl.jugg.util.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User utilities.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.3, Feb 7, 2012
 * @since 0.3.1
 */
public final class Users {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(Users.class.getName());
	/**
	 * User repository.
	 */
	private UserRepository userRepository = UserRepositoryImpl.getInstance();
	/**
	 * User service.
	 */
	private UserService userService = UserServiceFactory.getUserService();
	/**
	 * Article repository.
	 */
	private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();

	/**
	 * Determines whether if exists multiple users in current Solo.
	 * 
	 * @return {@code true} if exists, {@code false} otherwise
	 * @throws ServiceException
	 *             service exception
	 */
	public boolean hasMultipleUsers() throws ServiceException {
		final Query query = new Query().setPageCount(1);

		try {
			final JSONArray users = userRepository.get(query).getJSONArray(Keys.RESULTS);

			return 1 != users.length();
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Determines multiple users failed", e);

			throw new ServiceException(e);
		} catch (final JSONException e) {
			LOGGER.log(Level.SEVERE, "Determines multiple users failed", e);

			throw new ServiceException(e);
		}
	}

	/**
	 * Can the current user access an article specified by the given article id?
	 * 
	 * @param articleId
	 *            the given article id
	 * @param request
	 *            the specified request
	 * @return {@code true} if the current user can access the article,
	 *         {@code false} otherwise
	 * @throws Exception
	 *             exception
	 */
	public boolean canAccessArticle(final String articleId, final HttpServletRequest request) throws Exception {
		if (Strings.isEmptyOrNull(articleId)) {
			return false;
		}

		if (isAdminLoggedIn(request)) {
			return true;
		}

		final JSONObject article = articleRepository.get(articleId);
		final String currentUserEmail = getCurrentUser(request).getString(User.USER_EMAIL);

		if (!article.getString(Article.ARTICLE_AUTHOR_EMAIL).equals(currentUserEmail)) {
			return false;
		}

		return true;
	}

	/**
	 * Checks whether the current request is made by a logged in user (including
	 * default user and administrator lists in <i>users</i>).
	 * 
	 * <p>
	 * Invokes this method will try to login with cookie first.
	 * </p>
	 * 
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @return {@code true} if the current request is made by logged in user,
	 *         returns {@code false} otherwise
	 */
	public boolean isLoggedIn(final HttpServletRequest request, final HttpServletResponse response) {
		LoginProcessor.tryLogInWithCookie(request, response);

		final GeneralUser currentUser = userService.getCurrentUser(request);
		if (null == currentUser) {
			return false;
		}

		return isSoloUser(currentUser.getEmail()) || userService.isUserAdmin(request);
	}

	/**
	 * Checks whether the current request is made by logged in administrator.
	 * 
	 * @param request
	 *            the specified request
	 * @return {@code true} if the current request is made by logged in
	 *         administrator, returns {@code false} otherwise
	 */
	public boolean isAdminLoggedIn(final HttpServletRequest request) {
		return userService.isUserLoggedIn(request) && userService.isUserAdmin(request);
	}

	/**
	 * Gets the current user.
	 * 
	 * @param request
	 *            the specified request
	 * @return the current user, {@code null} if not found
	 */
	public JSONObject getCurrentUser(final HttpServletRequest request) {
		final GeneralUser currentUser = userService.getCurrentUser(request);
		if (null == currentUser) {
			return null;
		}

		final String email = currentUser.getEmail();

		try {
			return userRepository.getByEmail(email);
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Gets current user by request failed, returns null", e);

			return null;
		}
	}

	/**
	 * Determines whether the specified email is a user's email of this Solo
	 * application.
	 * 
	 * @param email
	 *            the specified email
	 * @return {@code true} if it is, {@code false} otherwise
	 */
	public boolean isSoloUser(final String email) {
		try {
			final Query query = new Query().setPageCount(1);
			final JSONObject result = userRepository.get(query);
			final JSONArray users = result.getJSONArray(Keys.RESULTS);

			return existEmail(email, users);
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Determines whether the specified email exits in the specified users.
	 * 
	 * @param email
	 *            the specified email
	 * @param users
	 *            the specified user
	 * @return {@code true} if exists, {@code false} otherwise
	 * @throws JSONException
	 *             json exception
	 */
	private boolean existEmail(final String email, final JSONArray users) throws JSONException {
		for (int i = 0; i < users.length(); i++) {
			final JSONObject user = users.getJSONObject(i);
			if (user.getString(User.USER_EMAIL).equalsIgnoreCase(email)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the {@link Users} singleton.
	 * 
	 * @return the singleton
	 */
	public static Users getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private default constructor.
	 */
	private Users() {
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
		private static final Users SINGLETON = new Users();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
