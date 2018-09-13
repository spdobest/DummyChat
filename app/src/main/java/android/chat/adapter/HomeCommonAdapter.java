package android.chat.adapter;

import android.chat.R;
import android.chat.listeners.StudentTeacherOrSubjectListener;
import android.chat.room.entity.UserOrGroupDetails;
import android.chat.util.Constants;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * Created by sibaprasad on 24/12/16.
 */

public class HomeCommonAdapter extends RecyclerView.Adapter<HomeCommonAdapter.ViewHolderParent> {

    public static final String TAG = "ChatAdapter";
    private LayoutInflater layoutInflater;

    private List<UserOrGroupDetails> listStudentOrTeacher;
    private List<String> listSubject;

    StudentTeacherOrSubjectListener studentTeacherOrSubjectListener;


    private Context context;

    private short rowType;

    public HomeCommonAdapter(Context context,short rowType,
                             StudentTeacherOrSubjectListener studentTeacherOrSubjectListener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.rowType = rowType;
        this.studentTeacherOrSubjectListener = studentTeacherOrSubjectListener;
    }

    @Override
    public ViewHolderParent onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == Constants.TAB_CONTACTS) {
            v = LayoutInflater.from(context)
                    .inflate(R.layout.itemview_student, parent, false);

            return new ViewHolderStudent(v);
        } else if (viewType == Constants.TAB_GROUP) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.itemview_student, parent, false);

            return new ViewHolderTeacher(v);
        } else if (viewType == 3) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.itemview_subject_list, parent, false);
            return new ViewHolderSubject(v);
        }
        else{
            return null;
        }

    }

    @Override
    public void onBindViewHolder(ViewHolderParent holder, int position) {

        switch (holder.getItemViewType()) {
            case Constants.TAB_CONTACTS:

                if(listStudentOrTeacher!=null && listStudentOrTeacher.size()>0) {
                    final UserOrGroupDetails userOrGroupDetails = listStudentOrTeacher.get(position);

                    final ViewHolderStudent viewHolderStudent = (ViewHolderStudent) holder;
                        viewHolderStudent.textViewName.setText(userOrGroupDetails.getName());

                    viewHolderStudent.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(studentTeacherOrSubjectListener!=null) {
                                studentTeacherOrSubjectListener.onSelectStudentOrTeacher(userOrGroupDetails);
                            }
                        }
                    });

                }
                break;

            case Constants.TAB_GROUP:
                final ViewHolderTeacher viewHolderTeacher = (ViewHolderTeacher) holder;
                if(listStudentOrTeacher!=null && listStudentOrTeacher.size()>0) {
                    final UserOrGroupDetails userOrGroupDetails = listStudentOrTeacher.get(position);

                    viewHolderTeacher.textViewName.setText(userOrGroupDetails.getName());

                    viewHolderTeacher.imageViewProfile.setImageDrawable(ContextCompat.getDrawable(
                            context,R.drawable.ic_people_grey
                    ));

                    viewHolderTeacher.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(studentTeacherOrSubjectListener!=null) {
                                studentTeacherOrSubjectListener.onSelectStudentOrTeacher(userOrGroupDetails);
                            }
                        }
                    });
                }
                break;
            case 3:
                final ViewHolderSubject viewHolderSubject = (ViewHolderSubject) holder;

                if(listSubject!=null && listSubject.size()>0) {
                    final String subject = listSubject.get(position);
                    viewHolderSubject.textViewSubjectName.setText(subject);


                    viewHolderSubject.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(studentTeacherOrSubjectListener!=null) {
                                studentTeacherOrSubjectListener.onSelectSubject(subject);
                            }
                        }
                    });

                }
                break;
        }
    }

    @Override
    public int getItemCount() {
       if(rowType == 1 || rowType ==2){
           return listStudentOrTeacher.size();
       }
       else{
           return  listSubject.size();
       }

    }

    @Override
    public int getItemViewType(int position) {
        return rowType;
    }

    public class ViewHolderParent extends RecyclerView.ViewHolder {
        public ViewHolderParent(View itemView) {
            super(itemView);
        }
    }

    public class ViewHolderStudent extends ViewHolderParent {
        AppCompatImageView imageViewProfile;
        AppCompatTextView textViewName;
        AppCompatTextView textViewSubject;
        AppCompatTextView textViewLastSeen;

        public ViewHolderStudent(View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewLastSeen = itemView.findViewById(R.id.textViewLastSeen);
            textViewSubject = itemView.findViewById(R.id.textViewSubject);
        }
    }

    public class ViewHolderTeacher extends ViewHolderParent {
        AppCompatImageView imageViewProfile;
        AppCompatTextView textViewName;
        AppCompatTextView textViewSubject;
        AppCompatTextView textViewLastSeen;

        public ViewHolderTeacher(View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewLastSeen = itemView.findViewById(R.id.textViewLastSeen);
            textViewSubject = itemView.findViewById(R.id.textViewSubject);
        }
    }

    public class ViewHolderSubject extends ViewHolderParent {
        AppCompatImageView imageViewProfile;
        AppCompatTextView textViewSubjectName;
        AppCompatTextView textViewTotalStudent;
        public ViewHolderSubject(View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewSubjectName = itemView.findViewById(R.id.textViewSubjectName);
            textViewTotalStudent = itemView.findViewById(R.id.textViewTotalStudent);
        }
    }

    public void setStudentOrData(List<UserOrGroupDetails> data){
        this.listStudentOrTeacher = data;
    }
    public void setSubjectData(List<String> data){
        this.listSubject = data;
    }
}
