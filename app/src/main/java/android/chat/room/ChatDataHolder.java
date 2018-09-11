package android.chat.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.chat.room.entity.ChatDto;
import android.chat.room.entity.User;
import android.chat.room.roomDao.ChatDtoDao;
import android.content.Context;


@Database(entities = {ChatDto.class, User.class}, version = 1)
public abstract class ChatDataHolder extends RoomDatabase {

    private static ChatDataHolder INSTANCE;

    public abstract ChatDtoDao chatDao();

    public static ChatDataHolder getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), ChatDataHolder.class, "user-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}