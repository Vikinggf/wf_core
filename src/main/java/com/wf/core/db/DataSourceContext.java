package com.wf.core.db;
/**
 * 动态数据源  切换用
 * @author chenlf
 */
public class DataSourceContext {
	public final static String DATA_SOURCE_READ = "dataSourceRead";
	public final static String DATA_SOURCE_WRITE = "dataSourceWrite";
	public final static String DATA_SOURCE_READ_MYCAT = "dataSourceReadMycat";
	public final static String DATA_SOURCE_WRITE_MYCAT = "dataSourceWriteMycat";

	private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

	public static void setCustomerType(String customerType) {
	        CONTEXT_HOLDER.set(customerType);
	    }

	public static String getCustomerType() {
	        return CONTEXT_HOLDER.get();
	    }  
	      
	public static void clearCustomerType() {
	        CONTEXT_HOLDER.remove();
	    }
	public static void setCustomerTypeByNo(String datasource,String shardNo) {
		String shard=datasource+shardNo;
		CONTEXT_HOLDER.set(shard);
	}
}
