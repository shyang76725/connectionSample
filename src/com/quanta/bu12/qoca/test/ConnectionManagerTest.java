package com.quanta.bu12.qoca.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.quanta.bu12.qoca.util.ConnectionManager;

import org.junit.Assert;

public class ConnectionManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ConnectionManager conn = new ConnectionManager();
		if(conn.queryForSingleInteger("select cast(count(0) as int)  from information_schema.tables"
				+ "		where"
				+ "			table_catalog = CURRENT_CATALOG and table_schema = CURRENT_SCHEMA"
				+ "			and table_name = 'conn_test_table'", null)>0){
			conn.sqlUpdate("DROP TABLE conn_test_table;",null);
		}
		
		conn.sqlUpdate("CREATE TABLE conn_test_table(  t01 text)WITH (  OIDS=FALSE);ALTER TABLE conn_test_table  OWNER TO shyang;",null);
		
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ConnectionManager conn = new ConnectionManager();
		if(conn.queryForSingleInteger("select cast(count(0) as int)  from information_schema.tables"
				+ "		where"
				+ "			table_catalog = CURRENT_CATALOG and table_schema = CURRENT_SCHEMA"
				+ "			and table_name = 'conn_test_table'", null)>0){
			conn.sqlUpdate("DROP TABLE conn_test_table;",null);
		}
		
	}
	@Before
	public void setUp() throws Exception {
		ConnectionManager conn = new ConnectionManager();
		conn.sqlUpdate("delete from conn_test_table", null);
	}
	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void connectionTest1() {
		ConnectionManager conn = new ConnectionManager();
		Assert.assertEquals(conn.queryForList("select 1 ", new HashMap()).size(), 1);
	}
	@Test
	public void connectionTest2() {
		ConnectionManager conn = new ConnectionManager();
		conn.setAutoCommit(false);
		try {
			conn.sqlUpdate("INSERT INTO conn_test_table(t01) VALUES ('s')", new HashMap());
			conn.transactionCommit();
		} catch (Exception e) {
			conn.transactionRollback();
		}
		Map dataMap = new HashMap();
		dataMap.put("var1", "s");
		Assert.assertEquals(conn.queryForSingleInteger("select cast(count(0) as int) from conn_test_table where t01=:var1 ", dataMap), 1);
	}
	@Test
	public void connectionTest3() {
		ConnectionManager conn = new ConnectionManager();
		conn.setAutoCommit(false);
		try {
			conn.sqlUpdate("INSERT INTO  conn_test_table(t01) VALUES ('s')", null);
			conn.sqlUpdate("INSERT INTO  conn_test_table(t01) VALUES ('s1')", null);
			conn.transactionCommit();
		} catch (Exception e) {
			conn.transactionRollback();
		}
		Map dataMap = new HashMap();
		dataMap.put("var1", "s");
		dataMap.put("var2", "s1");
		Assert.assertEquals(conn.queryForSingleInteger("select cast(count(0) as int) from conn_test_table where t01=:var1 ", dataMap), 1);
		Assert.assertEquals(conn.queryForSingleInteger("select cast(count(0) as int) from conn_test_table where t01=:var2 ", dataMap), 1);
	}
}
