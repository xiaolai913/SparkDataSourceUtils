package org.happy.utils.datasource.mysql;

import com.sun.tools.javac.util.Pair;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL读写工具类
 *
 * @author happy
 * @version 17/4/08 下午7:00
 */
public class MySQLConnector {
    private static Logger logger = LoggerFactory.getLogger(MySQLConnector.class);

    //    private BasicDataSource dataSource;
    private Connection conn;
    private String jdbcUrl;
    private String username;
    private String password;
    private QueryRunner runner;

    public MySQLConnector(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
//        init();
    }

    public void init() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            runner = new QueryRunner(true);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        return conn;
    }

    public QueryRunner getRunner() {
        return runner;
    }

    public List<Object[]> getDataList(String sql, Object[] params) throws SQLException {
        return runner.query(conn, sql, new ArrayListHandler(), params);
    }

    /**
     * 获取SQL查询结果并序列化成T类型对象列表, 注意T类型字段名需和数据库字段名保持一致
     *
     * @param sql
     * @param params
     * @param clazz
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> List<T> getDataBeanList(String sql, Object[] params, Class<T> clazz) throws SQLException {
        return runner.query(conn, sql, new BeanListHandler<T>(clazz), params);
    }

    /**
     * 获取SQL查询结果并序列化成T类型对象列表, 注意T类型字段名需和数据库字段名保持驼峰一致
     *
     * @param sql
     * @param params
     * @param clazz
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> List<T> getDataGenerousBeanList(String sql, Object[] params, Class<T> clazz) throws SQLException {
        BeanProcessor bean = new GenerousBeanProcessor();
        RowProcessor processor = new BasicRowProcessor(bean);
        BeanListHandler<T> h = new BeanListHandler<T>(clazz, processor);
        return runner.query(conn, sql, h, params);
    }

    public Object[] getData(String sql, Object[] params) throws SQLException {
        return runner.query(conn, sql, new ArrayHandler(), params);
    }

    public <T> T getDataBean(String sql, Object[] params, Class<T> clazz) throws SQLException {
        return runner.query(conn, sql, new BeanHandler<T>(clazz), params);
    }

    public <T> T getDataGenerousBean(String sql, Object[] params, Class<T> clazz) throws SQLException {
        BeanProcessor bean = new GenerousBeanProcessor();
        RowProcessor processor = new BasicRowProcessor(bean);
        BeanHandler<T> h = new BeanHandler<T>(clazz, processor);
        return runner.query(conn, sql, h, params);
    }

    public int insertData(String sql, Object[] entity) throws SQLException {
        return runner.update(conn, sql, entity);
    }

    public int[] batchInsertData(String sql, Object[][] entitys) throws SQLException {
        return runner.batch(conn, sql, entitys);
    }

    /**
     * 批量写入bean数据(注意:泛型类需要添加Table及Column相关注解以便进行字段映射)
     *
     * @param entitys
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> int[] batchInsertDataBean(List<T> entitys) throws Exception {
        if (entitys == null || entitys.size() == 0)
            return new int[]{-1};
        Pair<String, Object[][]> sqlAndParams = getInsertSqlAndParams(entitys);
        return runner.batch(conn, sqlAndParams.fst, sqlAndParams.snd);
    }

    /**
     * 根据待写入bean数据的类型信息得到其对应的批量插入SQL语句及参数
     *
     * @param entitys
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> Pair<String, Object[][]> getInsertSqlAndParams(List<T> entitys) throws Exception {
        Object[][] params = new Object[][]{};
        List<Object[]> records = new ArrayList<Object[]>();

        if (entitys.size() > 0) {
            T entityx = entitys.get(0);
            StringBuilder sb1 = new StringBuilder("INSERT INTO ");
            Table tb = entityx.getClass().getAnnotation(Table.class);
            sb1.append(tb.name()).append("(");

            StringBuilder sb2 = new StringBuilder("(");
            Method[] methods = entityx.getClass().getMethods();
            for (Method method : methods) {
                Column column = method.getAnnotation(Column.class);
                if (column != null && method.getName().startsWith("get")) {
                    try {
                        String columnName = column.name();
                        if (columnName != null) {
                            sb1.append(columnName).append(",");
                            sb2.append("?").append(",");
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            StringBuilder sql = new StringBuilder();
            if (sb1.length() > 0) {
                sb1.setCharAt(sb1.length() - 1, ')');
                sb2.setCharAt(sb2.length() - 1, ')');
            }
            sb1.append(" VALUES ");
            sql.append(sb1).append(sb2);

            for (T entity : entitys) {
                List<Object> fields = new ArrayList<Object>();
                for (Method method : methods) {
                    Column column = method.getAnnotation(Column.class);
                    if (column != null && method.getName().startsWith("get")) {
                        try {
                            String columnName = column.name();
                            Object obj = method.invoke(entity);
                            if (columnName != null) {
                                fields.add(obj);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                records.add(fields.toArray());
            }

            logger.debug(sql.toString());

            return new Pair<String, Object[][]>(sql.toString(), records.toArray(params));
        } else {
            return null;
        }
    }
}
