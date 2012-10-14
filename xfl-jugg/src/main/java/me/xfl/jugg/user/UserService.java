package me.xfl.jugg.user;

import javax.servlet.http.HttpServletRequest;

/**
 * User service.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Sep 27, 2011
 */
public interface UserService {

	/**
	 * Gets the current logged in user.
	 * 
	 * @param request
	 *            the specified request
	 * @return user if the user is logged in, return {@code null} otherwise
	 */
	GeneralUser getCurrentUser(final HttpServletRequest request);

	/**
	 * Determines whether the user logged in.
	 * 
	 * @param request
	 *            the specified request
	 * @return {@code true} if the user logged in, returns {@code false}
	 *         otherwise
	 */
	boolean isUserLoggedIn(final HttpServletRequest request);

	/**
	 * Determines whether the user is administrator.
	 * 
	 * @param request
	 *            the specified request
	 * @return {@code true} if the user is administrator, returns {@code false}
	 *         otherwise
	 */
	boolean isUserAdmin(final HttpServletRequest request);

	/**
	 * Creates login URL with the specified destination URL (redirect to the URL
	 * if login successfully).
	 * 
	 * @param destinationURL
	 *            the specified destination URL
	 * @return login URL
	 */
	String createLoginURL(final String destinationURL);

	/**
	 * Creates logout URL with the specified destination URL (redirect to the
	 * URL if logout successfully).
	 * 
	 * @param destinationURL
	 *            the specified destination URL
	 * @return login URL
	 */
	String createLogoutURL(final String destinationURL);
}
