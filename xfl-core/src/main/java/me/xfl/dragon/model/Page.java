package me.xfl.dragon.model;

/**
 * This class defines all page model relevant keys.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Apr 29, 2012
 * @since 0.3.1
 */
public final class Page {

	/**
	 * Page.
	 */
	public static final String PAGE = "page";
	/**
	 * Pages.
	 */
	public static final String PAGES = "pages";
	/**
	 * Key of title.
	 */
	public static final String PAGE_TITLE = "pageTitle";
	/**
	 * Key of content.
	 */
	public static final String PAGE_CONTENT = "pageContent";
	/**
	 * Key of order.
	 */
	public static final String PAGE_ORDER = "pageOrder";
	/**
	 * Key of comment count.
	 */
	public static final String PAGE_COMMENT_COUNT = "pageCommentCount";
	/**
	 * Key of permalink.
	 */
	public static final String PAGE_PERMALINK = "pagePermalink";
	/**
	 * Key of comments.
	 */
	public static final String PAGE_COMMENTS_REF = "pageComments";
	/**
	 * Key of comment-able.
	 */
	public static final String PAGE_COMMENTABLE = "pageCommentable";
	/**
	 * Key of page type.
	 * 
	 * <p>
	 * Available values:
	 * <ul>
	 * <li>link</li>
	 * No contents (pageContent), if users clicked, just jump to the given
	 * address specified by the permalink.
	 * <li>page</li>
	 * A normal customized page.
	 * </ul>
	 * </p>
	 */
	public static final String PAGE_TYPE = "pageType";
	/**
	 * Key of open target.
	 * 
	 * <p>
	 * Available values:
	 * <ul>
	 * <li>_blank</li>
	 * Opens the linked document in a new window or tab.
	 * <li>_self</li>
	 * Opens the linked document in the same frame as it was clicked (this is
	 * default).
	 * <li>_parent</li>
	 * Opens the linked document in the parent frame.
	 * <li>_top</li>
	 * Opens the linked document in the full body of the window.
	 * <li><i>frame name</i></li>
	 * Opens the linked document in a named frame.
	 * </ul>
	 * See <a href="http://www.w3schools.com/tags/att_a_target.asp">here</a> for
	 * more details.
	 * </p>
	 */
	public static final String PAGE_OPEN_TARGET = "pageOpenTarget";
	/**
	 * Key of page editor type.
	 * 
	 * @see Preference#EDITOR_TYPE
	 */
	public static final String PAGE_EDITOR_TYPE = "pageEditorType";

	/**
	 * Private default constructor.
	 */
	private Page() {
	}
}
