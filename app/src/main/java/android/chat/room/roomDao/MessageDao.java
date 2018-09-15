package android.chat.room.roomDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.chat.room.entity.MessageModel;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM messageTable")
    List<MessageModel> getAll();

/*    @Query("SELECT * FROM messageTable where senderName LIKE  :firstName AND recieverName LIKE :lastName")
    MessageModel findByName(String firstName, String lastName);*/

    @Query("SELECT COUNT(*) from messageTable")
    int countUsers();


    @Insert(onConflict = OnConflictStrategy.ABORT)
    Long insertMessage(MessageModel messageModel);


    @Delete
    void delete(MessageModel MessageModel);

    @Query("DELETE FROM messageTable")
    void deleteAll();

    @Query("SELECT * from messageTable ORDER BY id ASC")
    List<MessageModel> getAllChat();

    @Query("SELECT * FROM messageTable WHERE groupName LIKE :subject")
    public abstract List<MessageModel> getChatdataBySubject(String subject);

    @Query("SELECT * FROM messageTable WHERE senderId LIKE :recieverId AND currentUserId LIKE :currentUserId")
    public abstract List<MessageModel> getChatdataByRecieverId(String recieverId,String currentUserId);


    /**
     * Cursor cursor = db.rawQuery(
     "select * from " + DatabaseConstants.TABLE_USER + " where " + DatabaseConstants.USER_ID + " = ?",
     new String[]{ userId } );
     */
    @Query("SELECT * FROM messageTable WHERE groupName = :subject")
    public abstract List<MessageModel> getChatdataBySubject1(String subject);

    /**
     * DELETE FROM tableName where id NOT IN (SELECT id from tableName ORDER BY id DESC LIMIT 20)
     */

    @Query("SELECT messageDate from messageTable ORDER BY id DESC LIMIT 1")
    String  getLastMessageDate();

}