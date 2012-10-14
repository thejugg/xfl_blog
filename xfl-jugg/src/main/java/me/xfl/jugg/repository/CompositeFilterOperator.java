package me.xfl.jugg.repository;

import java.util.Arrays;

/**
 * Composite filter operator.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jun 27, 2012
 * @see CompositeFilter
 */
public enum CompositeFilterOperator {

	/**
	 * And.
	 */
	AND,
	/**
	 * Or.
	 */
	OR;

	/**
	 * Builds an composite filter with 'AND' all the specified sub filters.
	 * 
	 * @param subFilters
	 *            the specified sub filters
	 * @return composite filter
	 */
	public static CompositeFilter and(final Filter... subFilters) {
		return new CompositeFilter(AND, Arrays.asList(subFilters));
	}

	/**
	 * Builds an composite filter with 'OR' all the specified sub filters.
	 * 
	 * @param subFilters
	 *            the specified sub filters
	 * @return composite filter
	 */
	public static CompositeFilter or(final Filter... subFilters) {
		return new CompositeFilter(OR, Arrays.asList(subFilters));
	}
}
