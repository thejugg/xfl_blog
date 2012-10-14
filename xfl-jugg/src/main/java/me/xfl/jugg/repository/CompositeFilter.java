package me.xfl.jugg.repository;

import java.util.Collections;
import java.util.List;

/**
 * Composite filter that combines serval sub filters using a
 * {@link CompositeFilterOperator}.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jun 27, 2012
 * @see CompositeFilterOperator
 */
public final class CompositeFilter implements Filter {

	/**
	 * Operator.
	 */
	private CompositeFilterOperator operator;
	/**
	 * Sub filters.
	 */
	private List<Filter> subFilters;
	/**
	 * Initialization value for hashing.
	 */
	private static final int INIT_HASH = 3;
	/**
	 * Base for hashing.
	 */
	private static final int BASE = 97;

	/**
	 * Constructor with the specified parameters.
	 * 
	 * @param operator
	 *            the specified operator
	 * @param subFilters
	 *            the specified sub filters
	 */
	public CompositeFilter(final CompositeFilterOperator operator, final List<Filter> subFilters) {
		this.operator = operator;
		this.subFilters = subFilters;
	}

	/**
	 * Gets the sub filters.
	 * 
	 * @return sub filters
	 */
	public List<Filter> getSubFilters() {
		return Collections.unmodifiableList(subFilters);
	}

	/**
	 * Gets the operator.
	 * 
	 * @return operator
	 */
	public CompositeFilterOperator getOperator() {
		return operator;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CompositeFilter other = (CompositeFilter) obj;
		if (this.operator != other.operator) {
			return false;
		}
		if (this.subFilters != other.subFilters && (this.subFilters == null || !this.subFilters.equals(other.subFilters))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int ret = INIT_HASH;

		ret = BASE * ret + (this.operator != null ? this.operator.hashCode() : 0);
		ret = BASE * ret + (this.subFilters != null ? this.subFilters.hashCode() : 0);

		return ret;
	}
}
