package nutswall.db.util;

import nutswall.entity.User;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author chao11.ma
 *
 */
public class DbService {
	private DBOpenHelper openHelper;

	public DbService(Context context) {
		openHelper = new DBOpenHelper(context);
	}
	/**
	 * 获取用户数据
	 * @param path
	 * @return
	 */
	public User getUser(String username){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select username, password from usertable where username=?", new String[]{username});
		User u = new User();
		while(cursor.moveToNext()){
			u.setUsername(cursor.getString(0));
			u.setPassword(cursor.getString(1));
		}
		cursor.close();
		db.close();
		return u;
	}

}
