package online.himakeit.chitchat.stetho;

import android.content.Context;

import com.facebook.stetho.inspector.database.DatabaseConnectionProvider;
import com.facebook.stetho.inspector.database.DatabaseFilesProvider;
import com.facebook.stetho.inspector.database.SqliteDatabaseDriver;

/**
 * @author：LiXueLong
 * @date：2018/1/8
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class MyDatabaseDriver extends SqliteDatabaseDriver{
    public MyDatabaseDriver(Context context) {
        super(context);
    }

    public MyDatabaseDriver(Context context, DatabaseFilesProvider databaseFilesProvider, DatabaseConnectionProvider databaseConnectionProvider) {
        super(context, databaseFilesProvider, databaseConnectionProvider);
    }
}
