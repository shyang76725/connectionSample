package com.quanta.bu12.qoca.util;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.json.JSONObject;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;



public class ConnectionManager2 extends util{
	private DriverManagerDataSource driverManagerDataSource=null;
	private static HikariDataSource hikariDataSource = null;
	private static HikariConfig hikariConfig = null;
	private NamedParameterJdbcTemplate jdbcTemplate = null;
	private DataSourceTransactionManager transactionManager=null;
	private DefaultTransactionDefinition transactionDefinition=null;
	private TransactionStatus transactionStatus=null;
	private boolean autoCommit=true;
	
	public boolean isAutoCommit() {
		return autoCommit;
	}

	public ConnectionManager2() {
	}
	
	/**
	 * 為JdbcTemplate，將DriverManagerDataSource轉型為DataSource使用
	 * @return
	 */
	protected DataSource getDataSource() {
		return getHikariDataSource() ;
	}
	/**
	 * 建立DataSource
	 * @return
	 */
	private DriverManagerDataSource getDriverManagerDataSource() {
		if(driverManagerDataSource==null){
			driverManagerDataSource=new DriverManagerDataSource();
			driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
			driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5432/SHYANG_TEST");
			driverManagerDataSource.setUsername("shyang");
			driverManagerDataSource.setPassword("shyang");
			
		}
		return driverManagerDataSource;
	}
	private HikariConfig getHikariConfig() {
		if(hikariConfig==null){
			hikariConfig=new HikariConfig();
			hikariConfig.setDriverClassName("org.postgresql.Driver");
			hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/SHYANG_TEST");
			hikariConfig.setPassword("shyang");
			hikariConfig.setUsername("shyang");
			hikariConfig.setConnectionTestQuery("select 1");
			hikariConfig.setConnectionTimeout(10000);
			hikariConfig.setIdleTimeout(10000);
			hikariConfig.setMaxLifetime(0);
			hikariConfig.setMaximumPoolSize(100);
			hikariConfig.setMinimumIdle(10);
			hikariConfig.setLeakDetectionThreshold(2000);
		}
		return hikariConfig;
	}	
	private HikariDataSource getHikariDataSource() {
		if(hikariDataSource==null){
			hikariDataSource=new HikariDataSource(getHikariConfig());
		}
		return hikariDataSource;
	}	
	/**
	 * 建立NamedParameterJdbcTemplate
	 * @return
	 */
	protected NamedParameterJdbcTemplate getJdbcTemplate() {
		if(jdbcTemplate==null){
			jdbcTemplate=new NamedParameterJdbcTemplate(getDataSource());
		}
		return jdbcTemplate;
	}
	/**
	 * 建立JdbcTemplate
	 * @return
	 *//** 應該沒有作用
	protected JdbcTemplate getSimpleJdbcTemplate() {
		if(simpleJdbcTemplate==null){
			simpleJdbcTemplate= new JdbcTemplate(getDataSource());
		}
		return simpleJdbcTemplate;
	}*/
	/**
	 * 建立DataSourceTransactionManager(交易安全元件)
	 * @return
	 */
	protected DataSourceTransactionManager getTransactionManager() {
		if(transactionManager==null){
			transactionManager=new DataSourceTransactionManager(getDataSource());
		}
		return transactionManager;
	}
	/**
	 * 建立DefaultTransactionDefinition(交易安全元件)
	 * @return
	 */
	protected DefaultTransactionDefinition getTransactionDefinition() {
		if(transactionDefinition==null){
			transactionDefinition=new DefaultTransactionDefinition();
		}
		return transactionDefinition;
	}
	/**
	 * 開關交易安全 
	 * @param AutoCommit(true/false)
	 */
	public void setAutoCommit(boolean AutoCommit) {
		transactionDefinition = new DefaultTransactionDefinition();
		if (!AutoCommit && isAutoCommit()) { 
			transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		}else if(!isAutoCommit()){	
			transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_NEVER);
		}
		this.autoCommit=AutoCommit;
		setTransactionStatus(getTransactionManager().getTransaction(getTransactionDefinition()));
	}
	/**
	 * get交易安全狀態
	 * @return
	 */
	protected TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}
	/**
	 * set交易安全狀態
	 * @return
	 */
	protected void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	/**
	 * COMMIT交易安全
	 */
	public void transactionCommit() {
		getTransactionManager().commit(getTransactionStatus());
	}
	/**
	 * rollback交易安全
	 */
	public void transactionRollback() {
		if (isAutoCommit() == false && getTransactionManager() != null){
			getTransactionManager().rollback(getTransactionStatus());
		}
	}
	
	/**
	 * 查詢結果回傳成list
	 * @param sql sql語法
	 * @param obj 參數
	 * @return
	 */
	public List queryForList(String sql, Object obj) {
		List rows ;
		if (obj == null){
			rows =getJdbcTemplate().queryForList(sql, (new HashMap()));
		}else{
			if(obj instanceof MapSqlParameterSource ){
				rows = getJdbcTemplate().queryForList(sql, (MapSqlParameterSource)obj);
			}else if(obj instanceof JSONObject){
				rows = getJdbcTemplate().queryForList(sql, jsonObjParseMap((JSONObject)obj));
			}else if(obj instanceof Map ||obj instanceof HashMap ){
				rows = getJdbcTemplate().queryForList(sql, (Map)obj);
			}else{
				SqlParameterSource SqlParameterSource = new BeanPropertySqlParameterSource(obj);
				rows = getJdbcTemplate().queryForList(sql, SqlParameterSource);
			}
		}
		return rows;
	}
	/**
	 * 查詢結果回傳成單一數值
	 * @param sql sql語法
	 * @param obj 參數
	 * @return
	 */
	public int queryForSingleInteger(String sql, Object obj) {
		int data = -1;
		List rows = queryForList(sql, obj);
		if (rows.isEmpty()){
			return 0;
		}else{
			Iterator it = rows.iterator();
			if (it.hasNext()) {
				Map dataMap = (Map) it.next();
				Object[] keySet = dataMap.keySet().toArray();
				data = ((Integer) dataMap.get(keySet[0])).intValue();

			}
		}			
		return data;
	}
	/**
	 * 查詢結果回傳成單一字串
	 * @param sql sql語法
	 * @param obj 參數
	 * @return
	 */
	public String queryForSingleString(String sql, Object obj) {
		String data = "";
		List rows = queryForList(sql, obj);
		if (rows.isEmpty())
			data="";
		else {
			Iterator it = rows.iterator();
			if (it.hasNext()) {
				Map dataMap = (Map) it.next();
				Object[] keySet = dataMap.keySet().toArray();
				data = (String) dataMap.get(keySet[0]);
			}
		}
		if(data==null)data="";
		return data;
	}
	/**
	 * 執行更新語法
	 * @param sql sql語法
	 * @param obj 參數
	 * @return
	 */
	public int sqlUpdate(String sql, Object obj) {
		if(! ("").equals(sql)){
			
			if (obj == null){
				return getJdbcTemplate().update(sql, (new HashMap()));
			}else{
				if(obj instanceof MapSqlParameterSource ){
					return getJdbcTemplate().update(sql, (MapSqlParameterSource)obj);
				}else if(obj instanceof JSONObject ){
					return getJdbcTemplate().update(sql, jsonObjParseMap((JSONObject)obj));
				}else if(obj instanceof Map ||obj instanceof HashMap ){
					return getJdbcTemplate().update(sql, (Map)obj);
				}else{
					SqlParameterSource SqlParameterSource = new BeanPropertySqlParameterSource(obj);
					return getJdbcTemplate().update(sql, SqlParameterSource);
				}
			}
		}else {
			return 0;
		}

	}
}
