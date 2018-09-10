package android.chat.model;

public class Subject {

    public Subject(String subjectName, boolean isSelected) {
        this.subjectName = subjectName;
        this.isSelected = isSelected;
    }

    String subjectName;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    boolean isSelected;

}
