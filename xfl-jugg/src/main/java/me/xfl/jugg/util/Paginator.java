package me.xfl.jugg.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Paginator utilities.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, May 4, 2010
 */
public final class Paginator {

	/**
	 * Private default constructor.
	 */
	private Paginator() {
	}

	/**
	 * Paginates with the specified current page number, page size, page count,
	 * and window size.
	 * 
	 * @param currentPageNum
	 *            the specified current page number
	 * @param pageSize
	 *            the specified page size
	 * @param pageCount
	 *            the specified page count
	 * @param windowSize
	 *            the specified window size
	 * @return a list integer pagination page numbers
	 */
	public static List<Integer> paginate(final int currentPageNum, final int pageSize, final int pageCount, final int windowSize) {
		List<Integer> ret = null;

		if (pageCount < windowSize) {
			ret = new ArrayList<Integer>(pageCount);
			for (int i = 0; i < pageCount; i++) {
				ret.add(i, i + 1);
			}
		} else {
			ret = new ArrayList<Integer>(windowSize);
			int first = currentPageNum + 1 - windowSize / 2;
			first = first < 1 ? 1 : first;
			first = first + windowSize > pageCount ? pageCount - windowSize + 1 : first;
			for (int i = 0; i < windowSize; i++) {
				ret.add(i, first + i);
			}
		}

		return ret;
	}
}
