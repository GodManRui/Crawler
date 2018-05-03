package com.bonree.stock.data.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StockDatabaseHelper extends SQLiteOpenHelper {
	
	//数据库名
	private static final String DB_NAME = "stock.db";
	private static final int VERSION = 1;
	
	//表名
	private static final String EXCHANGE_QUANTITY_TABLE = "exchange_quantity";
	private static final String EXCHANGE_DETAIL_TABLE = "exchange_detail";
	
	public StockDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createExchangeQuantityTable();
		createExchangeDetailTable();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	/**
	 * 创建存储成交量的数据表
	 */
	private void createExchangeQuantityTable() {
		
	}
	
	/**
	 * 创建存储交易明细的数据表
	 */
	private void createExchangeDetailTable() {
		
	}
}
