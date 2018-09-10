package android.chat.listeners;

import android.chat.model.teacherSIgnup.SignupModel;

public interface StudentTeacherOrSubjectListener {
    void onSelectStudentOrTeacher(SignupModel signupModel);
    void onSelectSubject(String subjectName);
}
