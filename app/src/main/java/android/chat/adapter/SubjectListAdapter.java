package android.chat.adapter;

import android.chat.R;
import android.chat.listeners.SubjectSelectListener;
import android.chat.model.Subject;
import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.List;

/**
 * Created by sibaprasad on 01/01/17.
 */

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.ViewHolderSubject > {

    private Context context;
    private List<Subject> listData;
    LayoutInflater layoutInflater;
    SubjectSelectListener subjectSelectListener;

    public SubjectListAdapter(Context context, List< Subject > listData,SubjectSelectListener selectListener ) {
        this.context = context;
        this.subjectSelectListener=selectListener;
        this.listData = listData;
        layoutInflater = LayoutInflater.from( context );
    }

    @Override
    public  ViewHolderSubject onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view           = layoutInflater.inflate( R.layout.itemview_subject, parent, false );
        ViewHolderSubject chatViewHolder = new ViewHolderSubject( view );
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(final  ViewHolderSubject holder,final int position ) {
        final Subject subject = listData.get(position);
        if(subject!=null){
            holder.checkBoxSubjectName.setText(subject.getSubjectName());
            if(subject.isSelected()){
                holder.checkBoxSubjectName.setChecked(true);
            }
            else{
                holder.checkBoxSubjectName.setChecked(false);
            }


            holder.checkBoxSubjectName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        subjectSelectListener.onSubjectSelect(subject.getSubjectName(),b);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolderSubject extends RecyclerView.ViewHolder{
        AppCompatCheckBox checkBoxSubjectName;
        public ViewHolderSubject( View itemView ) {
            super( itemView );
            checkBoxSubjectName = itemView.findViewById(R.id.checkBoxSubjectName);
        }
    }
}
