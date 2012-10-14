package me.xfl.dragon.util.comparator;

import java.util.Comparator;

import me.xfl.dragon.model.Tag;

import org.json.JSONObject;

/**
 * Tag comparator by reference count.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Dec 30, 2010
 */
public final class TagRefCntComparator implements Comparator<JSONObject> {

	/**
	 * Package default constructor.
	 */
	TagRefCntComparator() {
	}

	@Override
	public int compare(final JSONObject tag1, final JSONObject tag2) {
		try {
			final Integer refCnt1 = tag1.getInt(Tag.TAG_REFERENCE_COUNT);
			final Integer refCnt2 = tag2.getInt(Tag.TAG_REFERENCE_COUNT);

			return refCnt1.compareTo(refCnt2);
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
