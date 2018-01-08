package online.himakeit.chitchat.stetho;

import android.content.Context;

import com.facebook.stetho.inspector.database.DatabaseFilesProvider;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：LiXueLong
 * @date：2018/1/8
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class MyDatabaseFilesProvider implements DatabaseFilesProvider {

    private Context context;

    public MyDatabaseFilesProvider(Context context) {
        this.context = context;
    }

    public static FilenameFilter myDBFilenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            if ("IMKitUserInfoCache".equals(name)
                    || "SealUserInfo".equals(name)
                    || "storage".equals(name)
                    || ".db".equals(name)) {
                return true;
            }
            return false;
        }
    };

    @Override
    public List<File> getDatabaseFiles() {
        List<File> dbFiles = new ArrayList<>();
        File filesDir = context.getFilesDir();
        dbFiles.addAll(listFiles(filesDir, myDBFilenameFilter));

        List<File> databaseFiles = new ArrayList<>();
        for (String databaseName : context.databaseList()) {
            databaseFiles.add(context.getDatabasePath(databaseName));
        }
        dbFiles.addAll(databaseFiles);
        return dbFiles;
    }

    private List<File> listFiles(File dir, FilenameFilter filenameFilter) {
        if (dir == null || dir.isFile()) {
            return null;
        }

        List<File> fileList = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && filenameFilter.accept(dir, file.getName())) {
                    fileList.add(file);
                } else {
                    List<File> temp = listFiles(file, filenameFilter);
                    if (temp != null && temp.size() > 0) {
                        fileList.addAll(temp);
                    }
                }
            }
        }

        return fileList;
    }
}
