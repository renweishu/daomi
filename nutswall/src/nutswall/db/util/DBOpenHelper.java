package nutswall.db.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "nutswall.db";
	private static final int VERSION = 1;
	// 调用父类构造器
	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}
	// 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行.
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建 用户表
		db.execSQL("CREATE TABLE IF NOT EXISTS usertable ( username varchar(10), password varchar(10))");
		// 初始化用户数据登录数据
		db.execSQL("insert into usertable(username, password) values('system','system')");
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	// 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS usertable");
		onCreate(db);
	}

}
