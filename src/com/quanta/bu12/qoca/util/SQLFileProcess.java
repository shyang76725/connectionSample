package com.quanta.bu12.qoca.util;

import java.util.HashMap;
import java.util.Map;


import net.sf.json.JSONObject;

public class SQLFileProcess extends util{
	private ConnectionManager conn=null;

	public ConnectionManager getConn() {
		if(conn==null){
			conn= new ConnectionManager();
		}
		return conn;
	}
	/**
	 * 系統SQL資料檔主程式method
	 * @param mainTable 主表名 SQLFILE.SF001
	 * @param no		組數 SQLFILE.SF002
	 * @param Type		類型 SQLFILE.SF003
	 * @return 內容 SQLFILE.SF004
	 */
	private String getSQLFileData(String mainTable,String no,String Type){
		String queryString=" SELECT sf004 FROM sqlfile WHERE sf001=:SF001 AND sf002=:SF002 AND sf003=:SF003 ";
		Map queryMap=new HashMap();
		queryMap.put("SF001", mainTable);
		queryMap.put("SF002", no);
		queryMap.put("SF003", Type);
		String sqlString=getConn().queryForSingleString(queryString, queryMap);
		return sqlString;
	}
	/**
	 * 取得開窗檔
	 * @param mainTable 主表名 SQLFILE.SF001
	 * @param no		組數 SQLFILE.SF002
	 * @param Type		類型 SQLFILE.SF003=0
	 * @return 內容 SQLFILE.SF004
	 */
	public String getWindowsSQLFile(String mainTable,String no){
		return getSQLFileData(mainTable,no,"0");
	}
	/**
	 * 取得檢核檔
	 * @param mainTable 主表名 SQLFILE.SF001
	 * @param no		組數 SQLFILE.SF002
	 * @param Type		類型 SQLFILE.SF003=1
	 * @return 內容 SQLFILE.SF004
	 */
	public String getCheckSQLFile(String mainTable,String no){
		return getSQLFileData(mainTable,no,"1");
	}
	/**
	 * 取得更新檔
	 * @param mainTable 主表名 SQLFILE.SF001
	 * @param no		組數 SQLFILE.SF002
	 * @param Type		類型 SQLFILE.SF003=2
	 * @return 內容 SQLFILE.SF004
	 */
	public String getUpdateSQLFile(String mainTable,String no){
		return getSQLFileData(mainTable,no,"2");
	}
	/**
	 * 取得檢核檔，並查詢檢核檔與傳入的資料(Map)
	 * @param mainTable 主表名 SQLFILE.SF001
	 * @param no		組數 SQLFILE.SF002
	 * @param Type		類型 SQLFILE.SF003=1
	 * @param checkData 檢核參數，根據需求傳入
	 * @return count的資料大於0，則true，反之false
	 */
	public boolean getCheckResultByCount(String mainTable,String no,Map checkData){
		boolean result;
		String checkSql = getSQLFileData(mainTable,no,"1");
		if(getConn().queryForSingleInteger(checkSql, checkData)>0){
			result=true;
		}else{
			result=false;
		}
		return result;
	}
	/**
	 * 取得檢核檔，並查詢檢核檔與傳入的資料(JSONObject)
	 * @param mainTable 主表名 SQLFILE.SF001
	 * @param no		組數 SQLFILE.SF002
	 * @param Type		類型 SQLFILE.SF003=1
	 * @param checkData 檢核參數，根據需求傳入
	 * @return count的資料大於0，則true，反之false
	 */
	public boolean getCheckResultByCount(String mainTable,String no,JSONObject checkData){
		return getCheckResultByCount(mainTable, no,jsonObjParseMap(checkData));
	}
	/**
	 * 取得更新檔，並查詢檢核檔與傳入的資料(JSONObject)
	 * @param mainTable 主表名 SQLFILE.SF001
	 * @param no		組數 SQLFILE.SF002
	 * @param Type		類型 SQLFILE.SF003=2
	 * @param updateData 更新參數，根據需求傳入
	 */
	public void executeUpdateSQLFile(String mainTable,String no,JSONObject updateData){
		executeUpdateSQLFile(mainTable, no,jsonObjParseMap(updateData));
	}
	/**
	 * 取得更新檔，並查詢檢核檔與傳入的資料(Map)
	 * @param mainTable 主表名 SQLFILE.SF001
	 * @param no		組數 SQLFILE.SF002
	 * @param Type		類型 SQLFILE.SF003=2
	 * @param updateData 更新參數，根據需求傳入
	 */
	public void executeUpdateSQLFile(String mainTable,String no,Map updateData){
		String updateSql = getSQLFileData(mainTable,no,"2");
		getConn().sqlUpdate(updateSql, updateData);
	}
}
