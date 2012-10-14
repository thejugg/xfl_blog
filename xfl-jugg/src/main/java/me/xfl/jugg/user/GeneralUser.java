package me.xfl.jugg.user;

/**
 * User.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Jul 10, 2011
 */
public final class GeneralUser {

	/**
	 * Id.
	 */
	private String id;
	/**
	 * Email.
	 */
	private String email;
	/**
	 * Nickname.
	 */
	private String nickname;

	/**
	 * Gets the email.
	 * 
	 * @return email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email with the specified email.
	 * 
	 * @param email
	 *            the specified email
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Gets the id.
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id with the specified id.
	 * 
	 * @param id
	 *            the specified id
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Gets the nickname.
	 * 
	 * @return nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Sets the nickname with the specified nickname.
	 * 
	 * @param nickname
	 *            the specified nickname
	 */
	public void setNickname(final String nickname) {
		this.nickname = nickname;
	}
}
