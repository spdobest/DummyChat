package android.chat.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.chat.room.entity.UserOrGroupDetails;
import android.chat.room.entity.MessageModel;
import android.chat.room.roomDao.MessageDao;
import android.chat.room.roomDao.UserOrGroupDao;
import android.content.Context;

@Database(entities = {MessageModel.class, UserOrGroupDetails.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract MessageDao getMessageDao();

    public abstract UserOrGroupDao getUserOrGroupDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public void databaseClose() {
        if (INSTANCE.isOpen()) {
            INSTANCE.close();
            AppDatabase.destroyInstance();
        }
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}