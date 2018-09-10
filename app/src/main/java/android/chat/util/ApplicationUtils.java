package android.chat.util;

import android.chat.model.Subject;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationUtils {

    public static List<Subject> getSubjectList(){

        List<Subject> subjectList = new ArrayList<>();
        subjectList.add(new Subject(Constants.SUBJECT_ANDROID,false));
        subjectList.add(new Subject(Constants.SUBJECT_IPHONE,false));
        subjectList.add(new Subject(Constants.SUBJECT_JAVA,false));
        subjectList.add(new Subject(Constants.SUBJECT_PYTHON,false));

        return subjectList;
    }


    public static List<String> getListSeparedByComma(String strWithComma){
        List<String> fixedLenghtList = null;
        if(!TextUtils.isEmpty(strWithComma)){
            if(strWithComma.contains(",")) {
                String[] elements = strWithComma.split(",");
                fixedLenghtList = Arrays.asList(elements);
                return fixedLenghtList;
            }
            else{
                fixedLenghtList = new ArrayList<>();
                fixedLenghtList.add(strWithComma);
            }
        }
        return fixedLenghtList;
    }

    public static String getStringWithComma(List<String> fixedLenghtList){
        StringBuilder stringBuilder = new StringBuilder();
        if(fixedLenghtList!=null && fixedLenghtList.size()>0){
            if(fixedLenghtList.size()==1){
                return fixedLenghtList.get(0);
            }
            else {
                for (String s : fixedLenghtList) {
                    stringBuilder.append(s + ",");
                }

                String str = stringBuilder.substring(0,stringBuilder.length()-1);

                return str;
            }

        }
        return "";
    }

    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s +" DELETED ");
                }
            }
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}
