package android.chat.model.teacher;

import android.content.Context;

import java.util.List;


/**
 * Created by sibaprasad on 10/04/18.
 */

public class TeacherLocalData {

    private static TeacherLocalData instance = null;
    private static Context context;

    public TeacherData getTeacherData() {
        return teacherData;
    }

    public void setTeacherData(TeacherData teacherData) {
        this.teacherData = teacherData;
    }

    private TeacherData teacherData;

    public TeacherLocalData newInstance(Context mCotext) {
        context = mCotext;
        if (instance == null)
            instance = new TeacherLocalData();
        return instance;
    }

}
