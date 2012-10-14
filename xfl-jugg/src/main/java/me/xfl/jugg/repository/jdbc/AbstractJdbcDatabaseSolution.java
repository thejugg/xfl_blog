package me.xfl.jugg.repository.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import me.xfl.jugg.repository.jdbc.util.Connections;
import me.xfl.jugg.repository.jdbc.util.JdbcUtil;

/**
 * 
 * JdbcDatabaseSolution.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Mar 9, 2012
 */
public abstract class AbstractJdbcDatabaseSolution implements JdbcDatabase {

	/**
	 * the map Mapping type to real database type.
	 */
	// 2012-8-7 9:40 private Map<String, Mapping> jdbcTypeMapping = new
	// HashMap<String, Mapping>();

	/**
	 * 
	 * register type to mapping solution.
	 * 
	 * @param type
	 *            type from json
	 * @param mapping
	 *            {@link Mapping}
	 */
	// 2012-8-7 9:40 public void registerType(final String type, final Mapping
	// mapping) {
	// jdbcTypeMapping.put(type, mapping);
	// }

	// @Override
	// 2012-8-7 9:40 public boolean createTable(final String tableName, final
	// List<FieldDefinition> fieldDefinitions) throws SQLException {
	// final Connection connection = Connections.getConnection();
	//
	// try {
	// // need config
	// // final StringBuilder dropTableSql = new StringBuilder();
	// // createDropTableSql(dropTableSql, tableName);
	// // JdbcUtil.executeSql(dropTableSql.toString(), connection);
	//
	// final StringBuilder createTableSql = new StringBuilder();
	//
	// createTableHead(createTableSql, tableName);
	// createTableBody(createTableSql, fieldDefinitions);
	// createTableEnd(createTableSql);
	//
	// return JdbcUtil.executeSql(createTableSql.toString(), connection);
	// } catch (final SQLException e) {
	// throw e;
	// } finally {
	// connection.close();
	// }
	// }

	/**
	 * 
	 * abstract createTableHead for each DB to impl.
	 * 
	 * @param dropTableSql
	 *            dropTableSql
	 * @param tableName
	 *            talbename
	 */
	// 2012-8-7 9:40 protected abstract void createDropTableSql(StringBuilder
	// dropTableSql,
	// String tableName);

	/**
	 * 
	 * abstract createTableHead for each DB to impl.
	 * 
	 * @param createTableSql
	 *            createSql
	 * @param tableName
	 *            tableName
	 */
	// 2012-8-7 9:40 protected abstract void createTableHead(StringBuilder
	// createTableSql,
	// String tableName);

	/**
	 * abstract createTableBody for each DB to impl.
	 * 
	 * @param createTableSql
	 *            createSql
	 * @param fieldDefinitions
	 *            {@link FieldDefinition}
	 */
	// 2012-8-7 9:40 protected abstract void createTableBody(StringBuilder
	// createTableSql,
	// List<FieldDefinition> fieldDefinitions);

	/**
	 * abstract createTableEnd for each DB to impl.
	 * 
	 * @param createTableSql
	 *            createSql
	 */
	// 2012-8-7 9:40 protected abstract void createTableEnd(StringBuilder
	// createTableSql);

	@Override
	public boolean clearTable(final String tableName, final boolean ifdrop) throws SQLException {

		final Connection connection = Connections.getConnection();
		try {
			final StringBuilder clearTableSql = new StringBuilder();
			clearTableSql(clearTableSql, tableName, ifdrop);
			return JdbcUtil.executeSql(clearTableSql.toString(), connection);

		} catch (final SQLException e) {
			throw e;
		} finally {
			connection.close();
		}
	}

	/**
	 * the clearTableSql for each Db to impl.
	 * 
	 * @param clearTableSql
	 *            clearTableSql
	 * @param tableName
	 *            tableName
	 * @param ifdrop
	 *            ifdrop
	 */
	public abstract void clearTableSql(final StringBuilder clearTableSql, final String tableName, final boolean ifdrop);

	/**
	 * 
	 * @return jdbcTypeMapping
	 */
	// 2012-8-7 9:40 public Map<String, Mapping> getJdbcTypeMapping() {
	// return jdbcTypeMapping;
	// }
}
