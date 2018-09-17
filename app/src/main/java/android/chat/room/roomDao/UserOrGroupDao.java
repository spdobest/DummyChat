package android.chat.room.roomDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.chat.room.entity.UserOrGroupDetails;
import java.util.List;

@Dao
public interface UserOrGroupDao {

    @Query("SELECT * FROM UserGroupTable WHERE isGroup = 0 ")
    public abstract List<UserOrGroupDetails> getAllSContacts();

/*    @Query("SELECT * FROM messageTable where senderName LIKE  :firstName AND recieverName LIKE :lastName")
    UserOrGroupDetails findByName(String firstName, String lastName);*/

    @Query("SELECT COUNT(*) from UserGroupTable")
    int countUsers();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertUser(UserOrGroupDetails UserOrGroupDetails);


    @Delete
    void delete(UserOrGroupDetails UserOrGroupDetails);

    @Query("DELETE FROM messageTable")
    void deleteAll();

    @Query("SELECT * from usergrouptable ORDER BY name ASC")
    List<UserOrGroupDetails> getAllUser();

    @Query("SELECT * FROM UserGroupTable WHERE isGroup = 1")
    public abstract List<UserOrGroupDetails> getAllGroup();

    @Query("SELECT * FROM UserGroupTable WHERE isTeacher = 1")
    public abstract List<UserOrGroupDetails> getAllTeacher();

    @Query("SELECT * FROM UserGroupTable WHERE isTeacher = 1 AND name LIKE :subjectName")
    public abstract List<UserOrGroupDetails> getAllTeacherBySubject(String subjectName);


    @Query("SELECT * FROM UserGroupTable WHERE isTeacher = 1 AND name LIKE :subjectName")
    public abstract List<UserOrGroupDetails> getAllStudentBySubject(String subjectName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertSubject(UserOrGroupDetails userOrGroupDetails);

    @Query("SELECT * FROM UserGroupTable WHERE isGroup = 1 ")
    public abstract List<UserOrGroupDetails> getAllSubject();

    @Query("SELECT userId FROM usergrouptable WHERE name = :subjectName LIMIT 1")
    String getSubjectName( String subjectName);

    @Query("SELECT userId FROM usergrouptable WHERE userId = :userId LIMIT 1")
    String getUserId( String userId);
}