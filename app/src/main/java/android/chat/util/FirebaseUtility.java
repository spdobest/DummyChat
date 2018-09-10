package android.chat.util;

import android.chat.application.ChatApplication;
import android.chat.listeners.OnFirebasseActionListener;
import android.chat.model.student.StudentData;
import android.chat.model.teacher.TeacherData;
import android.chat.model.teacher.TeacherLocalData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
 
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/**
 * Created by sibaprasad on 07/04/18.
 */

public class FirebaseUtility {

    static int yearCount = 0;

    private static List<StudentData> listStudentsFirstYear = new ArrayList<>();
    private static List<StudentData> listStudentsSecondYear = new ArrayList<>();
    private static List<StudentData> listStudents3rdYear = new ArrayList<>();
    private static List<StudentData> listStudents4thYear = new ArrayList<>();
    private static List<TeacherData> listTeacher = new ArrayList<>();

   


    private static TeacherData teacherProfileData ;
    private static StudentData studentProfileData;


    private static final String TAG = "FirebaseUtility";

    public static void saveTeacherProfile(final Context mContext,String teacherId){

        // teacherId = "-L9YZfr4aq4SLa7Uq2-a";
        Query teacherDetailsQuery = ChatApplication.getFirebaseDatabaseReference().
                child(FirebaseConstants.TEACHER_TABLE).orderByChild(FirebaseConstants.TEACHER_ID).equalTo(teacherId);
        teacherDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TeacherData teacherData = null;
                //_______________________________ check if server return null _________________________________
                if (dataSnapshot.exists()) {
                    //getting the data a t specific node which is selected by input Restaurant name
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        teacherData = child.getValue(TeacherData.class);
                        saveTeacherProfile(teacherData);
                    }
                    String name = teacherData.getName();
                    Log.i(TAG, "onDataChange: name from id is:  "+name);
                } else {
                    Log.i(TAG, " name does'nt exists!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void saveTeacherProfile(TeacherData teacherData){
        teacherProfileData = teacherData;
        if(teacherData!=null){
            String userId = teacherData.getTeacherId();
            ChatApplication.getFirebaseDatabaseReference().child(FirebaseConstants.TEACHER_TABLE).child(userId)
                    .setValue(teacherData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "onComplete: success ");
                            } else {
                                Log.i(TAG, "onComplete: fail");
                            }
                        }
                    });
        }

    }

    public static void updateTeacherProfileData(String subjects){
        if(teacherProfileData!=null){
            String mySubjects = teacherProfileData.getSubjects();
            mySubjects = mySubjects+","+subjects;
            teacherProfileData.setSubjects(mySubjects);
            saveTeacherProfile(teacherProfileData);
        }
    }


    public static void saveStudentProfile(final String studentId,String year){

        Query studentDetailsQuery = ChatApplication.getFirebaseDatabaseReference().
                child(FirebaseConstants.STUDENT_TABLE+year).orderByChild(FirebaseConstants.STUDENT_ID).equalTo(studentId);
        studentDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StudentData studentData = null;
                //_______________________________ check if server return null _________________________________
                if (dataSnapshot.exists()) {
                    //getting the data at specific node which is selected by input Restaurant name
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        studentData = child.getValue(StudentData.class);
                        saveStudentProfile(studentData);

                    }
                    String name = studentData.getName();
                    Log.i(TAG, "onDataChange: name from id is:  "+name);
                } else {
                    yearCount++;
                    if(yearCount==1){
                        saveStudentProfile(studentId,FirebaseConstants.SECOND_YEAR);
                    }
                    else if(yearCount==2){
                        saveStudentProfile(studentId,FirebaseConstants.THIRD_YEAR);

                    }
                    else{
                        saveStudentProfile(studentId,FirebaseConstants.FOURTH_YEAR);
                    }

                    Log.i(TAG, " name does'nt exists!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getDetails());
            }
        });
    }

    public static void saveStudentProfile(StudentData studentData){
        studentProfileData = studentData;
    }

    public static TeacherData getTeacherProfileData(){
        return teacherProfileData;
    }

    public static StudentData getStudentProfileData(){
        return studentProfileData;
    }

    public static List<StudentData> getStudentListByYears(String year){

        switch (year){
            case FirebaseConstants.FIRSTYEAR :
                return listStudentsFirstYear;
            case FirebaseConstants.SECOND_YEAR :
                return listStudentsSecondYear;
            case FirebaseConstants.THIRD_YEAR :
                return listStudents3rdYear;
            case FirebaseConstants.FOURTH_YEAR :
                return listStudents4thYear;

        }
        return null;
    }


    public static List<TeacherData> getTeacherList(){
        return listTeacher;
    }


    /**
     * Update Student Here
     *
     * @param
     * @param studentData
     */
    public static void updateStudent(StudentData studentData){

        if(studentData!=null){
            String userId = studentData.getStudentId();
            if(TextUtils.isEmpty(studentData.getStudentId())){
                userId = ChatApplication.getFirebaseDatabaseReference().push().getKey();
                studentData.setStudentId(userId);
            }

            ChatApplication.getFirebaseDatabaseReference().child(FirebaseConstants.STUDENT_TABLE+studentData.getYear()).child(userId)
                    .setValue(studentData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "onComplete: success ");
                            } else {
                                Log.i(TAG, "onComplete: fail");
                            }
                        }
                    });
        }
    }
    /**
     * Update Teachers Subjects
     *
     * @param
     * @param
     */

    public static void updateTeacher(final TeacherData teacherData){
        if(teacherData!=null){
            String userId = teacherData.getTeacherId();
            if(TextUtils.isEmpty(userId)){
                userId = ChatApplication.getFirebaseDatabaseReference().push().getKey();
                teacherData.setTeacherId(userId);
            }

            ChatApplication.getFirebaseDatabaseReference().child(FirebaseConstants.TEACHER_TABLE).child(userId)
                    .setValue(teacherData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "onComplete: success ");

                                TeacherLocalData teacherLocalData = new TeacherLocalData();
                                teacherLocalData.setTeacherData(teacherData);
                             //   AppPreferences.setTeacherLocalData(ChatApplication.getContext(),teacherLocalData);

                            } else {
                                Log.i(TAG, "onComplete: fail");
                            }
                        }
                    });
        }
    }

    public static String getYearFromSubjects(Context context, String subject){
        String year = "";

        return year;
    }

    public static List<String> getListofSubjectFromYear(String year){
        List<String> listSubjects = new ArrayList<>();
        return listSubjects;
    }

    public static void updateAttendance(List<StudentData> studentDataList ){
        if(studentDataList!=null && studentDataList.size()>0){
            for(StudentData studentData : studentDataList){
                // update code
            }
        }
    }

    /**
     * get all the student list by year
     * @param year
     */

    public static void getStudentByYear(final String year,final OnFirebasseActionListener onFirebasseActionListen ){

        switch (year){
            case FirebaseConstants.FIRSTYEAR :
                listStudentsFirstYear.clear();
                break;
            case FirebaseConstants.SECOND_YEAR :
                listStudentsSecondYear.clear();
                break;
            case FirebaseConstants.THIRD_YEAR :
                listStudents3rdYear.clear();
                break;
            case FirebaseConstants.FOURTH_YEAR :
                listStudents4thYear.clear();
                break;

        }


        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
     //   DatabaseReference ref = database.child("profiles");

        database.child(FirebaseConstants.STUDENT_TABLE+year).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                     StudentData studentData = noteDataSnapshot.getValue(StudentData.class);

                    switch (year){
                        case FirebaseConstants.FIRSTYEAR :
                            listStudentsFirstYear.add(studentData);
                            break;
                        case FirebaseConstants.SECOND_YEAR :
                            listStudentsSecondYear.add(studentData);
                            break;
                        case FirebaseConstants.THIRD_YEAR :
                            listStudents3rdYear.add(studentData);
                            break;
                        case FirebaseConstants.FOURTH_YEAR :
                            listStudents4thYear.add(studentData);
                            break;
                    }

                    if(onFirebasseActionListen!=null){
                        onFirebasseActionListen.onSuccess();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*Query phoneQuery = ref.orderByChild(phoneNo).equalTo("+923336091371");
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    user = singleSnapshot.getValue(User.class);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });*/


        /*ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                Map<String, Object> hashMapObject = (HashMap<String,Object>) dataSnapshot.getValue();
                for (Map.Entry mapEntry : hashMapObject.entrySet()) {
                    Gson gson = new Gson();
                    String obj = gson.toJson(mapEntry.getValue());

                    StudentData studentData = new Gson().fromJson(obj, StudentData.class);

                    switch (year){
                        case FirebaseConstants.FIRSTYEAR :
                            listStudentsFirstYear.add(studentData);
                            break;
                        case FirebaseConstants.SECOND_YEAR :
                             listStudentsSecondYear.add(studentData);
                            break;
                        case FirebaseConstants.THIRD_YEAR :
                             listStudents3rdYear.add(studentData);
                            break;
                        case FirebaseConstants.FOURTH_YEAR :
                             listStudents4thYear.add(studentData);
                            break;
                    }

                    if(onFirebasseActionListen!=null){
                        onFirebasseActionListen.onSuccess();
                    }

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                StudentData studentData = dataSnapshot.getValue(StudentData.class);
                Log.i(TAG, "onChildChanged: "+studentData.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());

            }
        };
        ChatApplication.getFirebaseDatabaseReference().addChildEventListener(childEventListener);*/



       /*ChatApplication.getFirebaseDatabaseReference().child(FirebaseConstants.STUDENT_TABLE+year).orderByChild("year")
                .equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    listStudent.add(adSnapshot.getValue(StudentData.class));
                }
                Log.d(TAG, "no of ads for search is "+listStudent.size());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error trying to get student for " +year+ " "+databaseError);
            }
        });*/

    }

    /**
     * Get Teacher List
     */

    public static void getTotalTeacherListFromFirebase( ){

        listTeacher.clear();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                Map<String, Object> hashMapObject = (HashMap<String,Object>) dataSnapshot.getValue();
                for (Map.Entry mapEntry : hashMapObject.entrySet()) {
                    Gson gson = new Gson();
                    String obj = gson.toJson(mapEntry.getValue());
                    TeacherData studentData = gson.fromJson(obj, TeacherData.class);
                    listTeacher.add(studentData);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ChatApplication.getFirebaseDatabaseReference().addChildEventListener(childEventListener);



       /*ChatApplication.getFirebaseDatabaseReference().child(FirebaseConstants.STUDENT_TABLE+year).orderByChild("year")
                .equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    listStudent.add(adSnapshot.getValue(StudentData.class));
                }
                Log.d(TAG, "no of ads for search is "+listStudent.size());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error trying to get student for " +year+ " "+databaseError);
            }
        });*/

    }


    /**
     * This List will show TO Student Registration
     */
    public static List<String> getYearList(){
        List<String> listYears = new ArrayList<>();
        listYears.add(FirebaseConstants.FIRSTYEAR);
        listYears.add(FirebaseConstants.SECOND_YEAR);
        listYears.add(FirebaseConstants.THIRD_YEAR);
        listYears.add(FirebaseConstants.FOURTH_YEAR);
        return listYears;
    }

    public interface FirebaseConstants{
        String STUDENT_TABLE        = "Student-";
        String TEACHER_TABLE        = "Teacher";
        String ATTENDANCE_TABLE     = "Attendance-";
        String COURSE_TABLE         = "Course";
        String FIRSTYEAR            = "1stYear";
        String SECOND_YEAR          = "2ndYear";
        String THIRD_YEAR           = "3rdYear";
        String FOURTH_YEAR          = "4thYear";

        // key constants in database
        String STUDENT_ID              = "studentId";
        String EMAILID                 = "emailId";
        String NAME                    = "name";
        String MOBILENUMBER            = "mobileNumber";
        String PASSWORD                = "password";
        String SUBJECT                 = "subject";
        String TEACHER_ID              = "teacherId";
        String SUBJECT_NAME              = "subjectName";
    }

    /**
     * THIS IS ONLY FOR TESTING
     */

    public static void setTestTeacherData(){
        int j = 0;
        String[] stringArrayFirstYear = {"TeacherA","TeacherB","TeacherC","TeacherD","TeacherE","TeacherF",};
        for(int i = 0;i<stringArrayFirstYear.length;i++) {

           TeacherData teacherData = new TeacherData("",stringArrayFirstYear[i],stringArrayFirstYear[i]+"@gmail.com",
                   "984923423","","123456"
                   );

            FirebaseUtility.updateTeacher(teacherData);
        }
    }

    public interface OnAttendanceAlreadyTakenListener{
        void onAttendanceAlreadyTaken(boolean isAttendanceTaken);
    }


    public static void call(){

    }

}
