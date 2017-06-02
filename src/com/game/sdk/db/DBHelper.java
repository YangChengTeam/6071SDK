package com.game.sdk.db;

import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.utils.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		Logger.msg("DBHelper---onCreate---");
		
		// 保存登录的用户信息
		db.execSQL(
				"create table if not exists " + UserLoginInfodao.TABLENAME + "(_id integer primary key autoincrement,"
						+ UserLoginInfodao.USERNAME + " TEXT," + UserLoginInfodao.PASSWORD + " TEXT,"+UserLoginInfodao.IS_VALIDATE+" INTEGER ,"+UserLoginInfodao.ACCOUNT_TYPE+" INTEGER )");
		
		//保存账号密码信息
		/*db.execSQL(
				"create table if not exists " + UserAccountInfodao.TABLENAME + "(_id integer primary key autoincrement,"
						+ UserAccountInfodao.USERNAME + " text," + UserAccountInfodao.PASSWORD + " text)");*/
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logger.msg("DBHelper---onUpgrade---");
		//db.execSQL("ALTER TABLE "+UserLoginInfodao.TABLENAME+" ALTER COLUMN "+UserLoginInfodao.USERNAME+" TEXT "); //修改字段类型
		//db.execSQL("ALTER TABLE "+UserLoginInfodao.TABLENAME+" ALTER COLUMN "+UserLoginInfodao.PASSWORD+" TEXT "); //修改字段类型
		
		
		db.execSQL("ALTER TABLE "+UserLoginInfodao.TABLENAME+" RENAME TO olduserlogin ");
		db.execSQL(
				"create table if not exists " + UserLoginInfodao.TABLENAME + "(_id integer primary key autoincrement,"
						+ UserLoginInfodao.USERNAME + " TEXT," + UserLoginInfodao.PASSWORD + " TEXT,"+UserLoginInfodao.IS_VALIDATE+" INTEGER default 0,"+UserLoginInfodao.ACCOUNT_TYPE+" INTEGER default 1 )");
		
		db.execSQL("INSERT INTO "+UserLoginInfodao.TABLENAME + "(_id,"+UserLoginInfodao.USERNAME+","+UserLoginInfodao.PASSWORD+") SELECT _id,"+UserLoginInfodao.USERNAME+","+UserLoginInfodao.PASSWORD+" from olduserlogin ");
		db.execSQL("DROP TABLE olduserlogin");
		//db.execSQL("ALTER TABLE "+UserLoginInfodao.TABLENAME+" ADD "+UserLoginInfodao.IS_VALIDATE+" INTEGER default 0 "); //往表中增加一列
		//db.execSQL("ALTER TABLE "+UserLoginInfodao.TABLENAME+" ADD "+UserLoginInfodao.ACCOUNT_TYPE+" INTEGER default 1"); //往表中增加一列
	}

}
