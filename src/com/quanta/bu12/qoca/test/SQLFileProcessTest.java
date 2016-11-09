package com.quanta.bu12.qoca.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.quanta.bu12.qoca.util.ConnectionManager;
import com.quanta.bu12.qoca.util.SQLFileProcess;

public class SQLFileProcessTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ConnectionManager conn = new ConnectionManager();
		conn.sqlUpdate("INSERT INTO sqlfile(sf001, sf002, sf003, sf004, sf005, sf006)"
				+ "VALUES('SQLFileProcessTestUse','0001','0','This is Windows File','test use','test use')", null);
		conn.sqlUpdate("INSERT INTO sqlfile(sf001, sf002, sf003, sf004, sf005, sf006)"
				+ "VALUES('SQLFileProcessTestUse','0001','1','This is Check File','test use','test use')", null);
		conn.sqlUpdate("INSERT INTO sqlfile(sf001, sf002, sf003, sf004, sf005, sf006)"
				+ "VALUES('SQLFileProcessTestUse','0001','2','This is Update File','test use','test use')", null);
		conn.sqlUpdate("INSERT INTO sqlfile(sf001, sf002, sf003, sf004, sf005, sf006)"
				+ "VALUES('SQLFileProcessTestUse','0002','0','This is Windows File','test use','test use')", null);
		conn.sqlUpdate("INSERT INTO sqlfile(sf001, sf002, sf003, sf004, sf005, sf006)"
				+ "VALUES('SQLFileProcessTestUse','0002','1','SELECT COUNT(*) FROM sqlfile WHERE sf002=:SF002','test use','test use')", null);
		conn.sqlUpdate("INSERT INTO sqlfile(sf001, sf002, sf003, sf004, sf005, sf006)"
				+ "VALUES('SQLFileProcessTestUse','0002','2','INSERT INTO sqlfile(sf001, sf002, sf003, sf004, sf005, sf006)VALUES(:SF001, :SF002, :SF003, :SF004, :SF005, :SF006)','test use','test use')", null);
	}
	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ConnectionManager conn = new ConnectionManager();
		conn.sqlUpdate("DELETE from sqlfile WHERE sf001='SQLFileProcessTestUse'", null);
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getWindowsSQLFileTest1() {
		SQLFileProcess sfp = new SQLFileProcess();
		Assert.assertEquals(sfp.getWindowsSQLFile("SQLFileProcessTestUse", "0001"), "This is Windows File");
	}
	@Test
	public void getCheckSQLFileTest1() {
		SQLFileProcess sfp = new SQLFileProcess();
		Assert.assertEquals(sfp.getCheckSQLFile("SQLFileProcessTestUse", "0001"), "This is Check File");
	}
	@Test
	public void getUpdateSQLFileTest1() {
		SQLFileProcess sfp = new SQLFileProcess();
		Assert.assertEquals(sfp.getUpdateSQLFile("SQLFileProcessTestUse", "0001"), "This is Update File");
	}
	@Test
	public void getCheckSQLFileTest2() {
		SQLFileProcess sfp = new SQLFileProcess();
		Map dataMap = new HashMap();
		dataMap.put("SF002", "0002");
		Assert.assertEquals(sfp.getCheckResultByCount("SQLFileProcessTestUse", "0002",dataMap), true);
	}
	@Test
	public void getCheckSQLFileTest3() {
		SQLFileProcess sfp = new SQLFileProcess();
		Map dataMap = new HashMap();
		dataMap.put("SF002", "9999");
		Assert.assertEquals(sfp.getCheckResultByCount("SQLFileProcessTestUse", "0002",dataMap), false);
	}
	@Test
	public void getUpdateSQLFileTest2() {
		SQLFileProcess sfp = new SQLFileProcess();
		Map dataMap = new HashMap();
		dataMap.put("SF001", "SQLFileProcessTestUse");
		dataMap.put("SF002", "9999");
		dataMap.put("SF003", "2");
		dataMap.put("SF004", "test 9999");
		dataMap.put("SF005", "test use");
		dataMap.put("SF006", "test use");
		sfp.executeUpdateSQLFile("SQLFileProcessTestUse", "0002", dataMap);
		Map checkDataMap = new HashMap();
		checkDataMap.put("SF002", "9999");
		Assert.assertEquals(sfp.getCheckResultByCount("SQLFileProcessTestUse", "0002",dataMap), true);
	}
}
