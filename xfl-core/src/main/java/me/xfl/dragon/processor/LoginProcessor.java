package me.xfl.dragon.processor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.xfl.dragon.model.Common;
import me.xfl.dragon.repository.UserRepository;
import me.xfl.dragon.repository.impl.UserRepositoryImpl;
import me.xfl.jugg.annotation.RequestProcessing;
import me.xfl.jugg.annotation.RequestProcessor;
import me.xfl.jugg.model.User;
import me.xfl.jugg.servlet.HTTPRequestContext;
import me.xfl.jugg.servlet.HTTPRequestMethod;
import me.xfl.jugg.util.MD5;
import me.xfl.jugg.util.Sessions;
import me.xfl.jugg.util.Strings;

import org.json.JSONObject;

/**
 * Login/logout processor.
 * 
 * <p>
 * Initializes administrator
 * </p>
 * .
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.1.1.0, Aug 9, 2012
 * @since 0.3.1
 */
@RequestProcessor
public final class LoginProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(LoginProcessor.class.getName());
	/**
	 * User repository.
	 */
	private static UserRepository userRepository = UserRepositoryImpl.getInstance();

	/**
	 * Logout.
	 * 
	 * @param context
	 *            the specified context
	 * @throws IOException
	 *             io exception
	 */
	@RequestProcessing(value = { "/logout" }, method = HTTPRequestMethod.GET)
	public void logout(final HTTPRequestContext context) throws IOException {
		final HttpServletRequest httpServletRequest = context.getRequest();

		Sessions.logout(httpServletRequest, context.getResponse());

		String destinationURL = httpServletRequest.getParameter(Common.GOTO);
		if (Strings.isEmptyOrNull(destinationURL)) {
			destinationURL = "/";
		}

		context.getResponse().sendRedirect(destinationURL);
	}

	/**
	 * Tries to login with cookie.
	 * 
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 */
	public static void tryLogInWithCookie(final HttpServletRequest request, final HttpServletResponse response) {
		final Cookie[] cookies = request.getCookies();
		if (null == cookies || 0 == cookies.length) {
			return;
		}

		try {
			for (int i = 0; i < cookies.length; i++) {
				final Cookie cookie = cookies[i];

				if (!"b3log-latke".equals(cookie.getName())) {
					continue;
				}

				final JSONObject cookieJSONObject = new JSONObject(cookie.getValue());

				final String userEmail = cookieJSONObject.optString(User.USER_EMAIL);
				if (Strings.isEmptyOrNull(userEmail)) {
					break;
				}

				final JSONObject user = userRepository.getByEmail(userEmail.toLowerCase().trim());
				if (null == user) {
					break;
				}

				final String userPassword = user.optString(User.USER_PASSWORD);
				final String hashPassword = cookieJSONObject.optString(User.USER_PASSWORD);
				if (MD5.hash(userPassword).equals(hashPassword)) {
					Sessions.login(request, response, user);
					LOGGER.log(Level.INFO, "Logged in with cookie[email={0}]", userEmail);
				}
			}
		} catch (final Exception e) {
			LOGGER.log(Level.WARNING, "Parses cookie failed, clears the cookie[name=b3log-latke]", e);

			final Cookie cookie = new Cookie("b3log-latke", null);
			cookie.setMaxAge(0);
			cookie.setPath("/");

			response.addCookie(cookie);
		}
	}
}
