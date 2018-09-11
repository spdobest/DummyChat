package android.chat.listeners;

import android.chat.model.UserOrGroupDetails;

public interface StudentTeacherOrSubjectListener {
    void onSelectStudentOrTeacher(UserOrGroupDetails userOrGroupDetails);
    void onSelectSubject(String subjectName);
}
