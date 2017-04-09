package org.happy.utils.datasource.mysql;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * MySQL读写工具类单元测试
 *
 * @author happy
 * @version 17/4/08 下午7:00
 */
public class MySQLConnectorTest {

    MySQLConnector connector;

    @Before
    public void setUp() throws Exception {
        connector = new MySQLConnector("jdbc:mysql://localhost:3306/rcs", "root", "440211");
        connector.init();
    }

    @Test
    public void testGetDataList() throws Exception {
        List<Object[]> result = connector.getDataList("select * from person", null);
        for(Object[] row : result) {
            for(Object field : row)
                System.out.println(field.toString());
        }
    }

    @Test
    public void testGetDataBeanList() throws Exception {
        List<SimpleBeanDemo> result = connector.getDataBeanList("select * from person where full_name = ?", new Object[]{"zhangsan"}, SimpleBeanDemo.class);
        for(SimpleBeanDemo row : result) {
            System.out.println(row);
        }
    }

    @Test
    public void testGetDataGenerousBeanList() throws Exception {

    }

    @Test
    public void testGetData() throws Exception {

    }

    @Test
    public void testGetDataBean() throws Exception {

    }

    @Test
    public void testGetDataGenerousBean() throws Exception {

    }

    @Test
    public void testInsertOrUpdateData() throws Exception {

    }

    @Test
    public void testBatchInsertData() throws Exception {
        Object[][] params = new Object[][] { { "zhangsan", 18, "天津" }, { "lisi", 15, "上海" } };
        int[] result = connector.batchInsertData("insert into person (full_name, age, home_address) values (?, ?, ?)", params);
        System.out.println(result);
    }

    @Test
    public void testBatchInsertDataBean() throws Exception {
        List<SimpleBeanDemo> persons = new ArrayList<SimpleBeanDemo>();
        SimpleBeanDemo person1 = new SimpleBeanDemo("zhangsan", 18, "天津");
        SimpleBeanDemo person2 = new SimpleBeanDemo("lisi", 15, "上海");
        persons.add(person1);
        persons.add(person2);

        int[] result = connector.batchInsertDataBean(persons);
        System.out.println(result);
    }
}