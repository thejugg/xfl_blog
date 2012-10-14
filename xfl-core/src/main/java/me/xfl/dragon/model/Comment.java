package me.xfl.dragon.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * This class defines all comment model relevant keys.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Oct 28, 2011
 * @since 0.3.1
 */
public final class Comment {

	/**
	 * Comment.
	 */
	public static final String COMMENT = "comment";
	/**
	 * Comments.
	 */
	public static final String COMMENTS = "comments";
	/**
	 * Key of comment.
	 */
	public static final String COMMENT_CONTENT = "commentContent";
	/**
	 * Key of comment name.
	 */
	public static final String COMMENT_NAME = "commentName";
	/**
	 * Key of comment email.
	 */
	public static final String COMMENT_EMAIL = "commentEmail";
	/**
	 * Key of comment URL.
	 */
	public static final String COMMENT_URL = "commentURL";
	/**
	 * Key of comment sharp URL.
	 */
	public static final String COMMENT_SHARP_URL = "commentSharpURL";
	/**
	 * Key of comment date.
	 */
	public static final String COMMENT_DATE = "commentDate";
	/**
	 * Key of comment time.
	 */
	public static final String COMMENT_TIME = "commentTime";
	/**
	 * Key of comment thumbnail URL.
	 */
	public static final String COMMENT_THUMBNAIL_URL = "commentThumbnailURL";
	/**
	 * Key of original comment id.
	 */
	public static final String COMMENT_ORIGINAL_COMMENT_ID = "commentOriginalCommentId";
	/**
	 * Key of original comment user name.
	 */
	public static final String COMMENT_ORIGINAL_COMMENT_NAME = "commentOriginalCommentName";
	/**
	 * Key of comment on type.
	 */
	public static final String COMMENT_ON_TYPE = "commentOnType";
	/**
	 * Key of comment on id.
	 */
	public static final String COMMENT_ON_ID = "commentOnId";
	/**
	 * Date format(yyyy/MM/dd hh:mm:ss).
	 */
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	/**
	 * Private default constructor.
	 */
	private Comment() {
	}
}
