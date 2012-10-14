package me.xfl.jugg.repository.jdbc;

import java.sql.SQLException;
import java.util.List;

import me.xfl.jugg.repository.jdbc.util.FieldDefinition;


/**
 * interface JdbcDatabase.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Dec 20, 2011
 */
public interface JdbcDatabase {

    /**
     * createTable.
     * @param tableName tableName
     * @param fieldDefinitions fieldDefinitions
     * 
     * @return ifseccuss
     * @throws SQLException SQLException 
     */
	//暂时用不到创建表
//2012-8-7 9:40    boolean createTable(String tableName, List<FieldDefinition> fieldDefinitions)
//            throws SQLException;

    /**
     * 
     * @param tableName tableName
     * @param ifdrop ifdrop 
     * <P>
     *  ifdrop true: using drop
     *         not: using truncate to clear data.
     * </p>
     * @throws SQLException   SQLException
     * @return if success to clearTable
     */
    boolean clearTable(final String tableName, final boolean ifdrop) throws SQLException;

    /**
     * queryPage sql.
     * 
     * @param start start
     * @param end end
     * @param selectSql selectSql
     * @param filterSql filterSql
     * @param orderBySql orderBySql
     * @param tableName tableName
     * @return sql 
     */
    String queryPage(int start, int end, String selectSql, String filterSql,
            String orderBySql, String tableName);

    /**
     * getRandomlySql.
     * 
     * @param tableName tableName
     * @param fetchSize fetchSize
     * @return sql sql
     */
    String getRandomlySql(final String tableName, int fetchSize);

}
