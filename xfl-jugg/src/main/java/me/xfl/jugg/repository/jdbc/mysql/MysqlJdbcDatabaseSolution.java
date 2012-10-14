package me.xfl.jugg.repository.jdbc.mysql;

import me.xfl.jugg.repository.jdbc.AbstractJdbcDatabaseSolution;

import org.apache.commons.lang.StringUtils;

/**
 * DefaultJdbcDatabaseSolution,for extend .
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, May 8, 2012
 */
public class MysqlJdbcDatabaseSolution extends AbstractJdbcDatabaseSolution {

	/**
	 * Public constructor.
	 */
	public MysqlJdbcDatabaseSolution() {
		// 2012-8-7 9:40 registerType("int", new IntMapping());
		// registerType("boolean", new BooleanMapping());
		// registerType("long", new LongMapping());
		// registerType("double", new NumberMapping());
		// registerType("String", new StringMapping());
		// registerType("Date", new DateMapping());
	}

	@Override
	public String queryPage(final int start, final int end, final String selectSql, final String filterSql, final String orderBySql,
			final String tableName) {
		final StringBuilder sql = new StringBuilder();
		sql.append(selectSql).append(" from ").append(tableName);
		if (StringUtils.isNotBlank(filterSql)) {
			sql.append(" where ").append(filterSql);
		}
		sql.append(orderBySql);
		sql.append(" limit ").append(start).append(",").append(end - start);
		return sql.toString();
	}

	@Override
	public String getRandomlySql(final String tableName, final int fetchSize) {
		final StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * FROM ").append(tableName).append(" ORDER BY RAND() LIMIT ").append(fetchSize);
		return sql.toString();
	}

	// @Override
	// 2012-8-7 9:40 protected void createDropTableSql(final StringBuilder
	// dropTableSql, final String tableName) {
	// dropTableSql.append("DROP TABLE  IF EXISTS ").append(tableName).append(";");
	//
	// }

	// @Override
	// 2012-8-7 9:40 protected void createTableHead(final StringBuilder
	// createTableSql, final String tableName) {
	// // createTableSql.append("DROP TABLE  IF EXISTS ").append(tableName)
	// // .append(";");
	// createTableSql.append("CREATE TABLE  IF NOT EXISTS ").append(tableName).append("(");
	//
	// }

	// @Override
	// 2012-8-7 9:40 protected void createTableBody(final StringBuilder
	// createTableSql, final List<FieldDefinition> fieldDefinitions) {
	// final List<FieldDefinition> keyDefinitionList = new
	// ArrayList<FieldDefinition>();
	// for (FieldDefinition fieldDefinition : fieldDefinitions) {
	//
	// final String type = fieldDefinition.getType();
	// if (type == null) {
	// throw new
	// RuntimeException("the type of fieldDefinitions should not be null");
	// }
	// final Mapping mapping = getJdbcTypeMapping().get(type);
	// if (mapping != null) {
	// createTableSql.append(mapping.toDataBaseSting(fieldDefinition)).append(",   ");
	//
	// if (fieldDefinition.getIsKey()) {
	// keyDefinitionList.add(fieldDefinition);
	// }
	// } else {
	// throw new RuntimeException("the type[" + fieldDefinition.getType() +
	// "] is not register for mapping ");
	// }
	//
	// }
	//
	// if (keyDefinitionList.size() < 0) {
	// throw new RuntimeException("no key talbe is not allow");
	// } else {
	// createTableSql.append(createKeyDefinition(keyDefinitionList));
	// }
	//
	// }

	/**
	 * the keyDefinitionList tableSql.
	 * 
	 * @param keyDefinitionList
	 *            keyDefinitionList
	 * @return createKeyDefinitionsql
	 */
	// 2012-8-7 9:40 private String createKeyDefinition(final
	// List<FieldDefinition> keyDefinitionList) {
	//
	// final StringBuilder sql = new StringBuilder();
	// sql.append(" PRIMARY KEY");
	// boolean isFirst = true;
	// for (FieldDefinition fieldDefinition : keyDefinitionList) {
	// if (isFirst) {
	// sql.append("(");
	// isFirst = false;
	// } else {
	// sql.append(",");
	// }
	// sql.append(fieldDefinition.getName());
	// }
	//
	// sql.append(")");
	// return sql.toString();
	// }

	// @Override
	// 2012-8-7 9:40 protected void createTableEnd(final StringBuilder
	// createTableSql) {
	// createTableSql.append(") ENGINE= InnoDB DEFAULT CHARSET= utf8;");
	//
	// }

	@Override
	public void clearTableSql(final StringBuilder clearTableSq, final String tableName, final boolean ifdrop) {
		if (ifdrop) {
			clearTableSq.append("DROP TABLE IF EXISTS ").append(tableName);
		} else {
			clearTableSq.append("TRUNCATE TABLE ").append(tableName);
		}
	}
}
