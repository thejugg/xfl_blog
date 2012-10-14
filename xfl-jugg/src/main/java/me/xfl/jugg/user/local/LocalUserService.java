package me.xfl.jugg.user.local;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import me.xfl.jugg.Juggs;
import me.xfl.jugg.Keys;
import me.xfl.jugg.model.Role;
import me.xfl.jugg.model.User;
import me.xfl.jugg.user.GeneralUser;
import me.xfl.jugg.user.UserService;
import me.xfl.jugg.util.Sessions;

import org.json.JSONObject;

/**
 * Local user service.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, May 4, 2012
 */
public final class LocalUserService implements UserService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(LocalUserService.class.getName());

	@Override
	public GeneralUser getCurrentUser(final HttpServletRequest request) {
		final JSONObject currentUser = Sessions.currentUser(request);
		if (null == currentUser) {
			return null;
		}

		final GeneralUser ret = new GeneralUser();
		ret.setEmail(currentUser.optString(User.USER_EMAIL));
		ret.setId(currentUser.optString(Keys.OBJECT_ID));
		ret.setNickname(currentUser.optString(User.USER_NAME));

		return ret;
	}

	@Override
	public boolean isUserLoggedIn(final HttpServletRequest request) {
		return null != Sessions.currentUser(request);
	}

	@Override
	public boolean isUserAdmin(final HttpServletRequest request) {
		final JSONObject currentUser = Sessions.currentUser(request);

		if (null == currentUser) {
			return false;
		}

		return Role.ADMIN_ROLE.equals(currentUser.optString(User.USER_ROLE));
	}

	@Override
	public String createLoginURL(final String destinationURL) {
		String to = Juggs.getServePath();

		try {
			to = URLEncoder.encode(to + destinationURL, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			LOGGER.log(Level.SEVERE, "URL encode[string={0}]", destinationURL);
		}

		return Juggs.getContextPath() + "/login?goto=" + to;
	}

	@Override
	public String createLogoutURL(final String destinationURL) {
		String to = Juggs.getServePath();

		try {
			to = URLEncoder.encode(to + destinationURL, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			LOGGER.log(Level.SEVERE, "URL encode[string={0}]", destinationURL);
		}

		return Juggs.getContextPath() + "/logout?goto=" + to;
	}
}
