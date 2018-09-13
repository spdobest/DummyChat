package android.chat.listeners;

import android.chat.room.entity.UserOrGroupDetails;

public interface StudentTeacherOrSubjectListener {
    void onSelectStudentOrTeacher(UserOrGroupDetails userOrGroupDetails);
    void onSelectSubject(String subjectName);
}
