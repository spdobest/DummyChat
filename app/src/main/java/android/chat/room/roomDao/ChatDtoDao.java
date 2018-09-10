package android.chat.room.roomDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.chat.room.entity.ChatDto;

import java.util.List;

@Dao
public interface ChatDtoDao {

    @Query("SELECT * FROM chatData")
    List<ChatDto> getAll();

    @Query("SELECT * FROM chatData where senderName LIKE  :firstName AND recieverName LIKE :lastName")
    ChatDto findByName(String firstName, String lastName);

    @Query("SELECT COUNT(*) from chatData")
    int countUsers();

    @Insert
    void insertAll(ChatDto... chatdata);

    @Delete
    void delete(ChatDto ChatDto);

    @Query("DELETE FROM chatData")
    void deleteAll();

    @Query("SELECT * from chatData ORDER BY id ASC")
    List<ChatDto> getAllChat();

    @Query("SELECT * FROM chatData WHERE subject LIKE :subject")
    public abstract List<ChatDto> getChatdataBySubject(String subject);

}