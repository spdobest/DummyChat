package android.chat.ui.dialogFragment

import android.chat.R
import android.chat.data.PreferenceManager
import android.chat.room.AppDatabase
import android.chat.room.entity.UserOrGroupDetails
import android.chat.ui.base.BaseDialogFragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

public class ProfileDialogFragment:BaseDialogFragment(){

    lateinit var imageViewProfile:AppCompatImageView
    lateinit var edittextUserName:AppCompatEditText
    lateinit var edittextEmail:AppCompatEditText
    lateinit var edittextMobileNumber: AppCompatEditText
    lateinit var switchCompatIsTeacher: SwitchCompat
    lateinit var textViewSubjectList: AppCompatTextView


    private val listSubjects = java.util.ArrayList<UserOrGroupDetails>()

    /**
     * DATABASE
     */
    private var appDatabase: AppDatabase? = null


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appDatabase = AppDatabase.getAppDatabase(activity)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setLayout(R.layout.dialogfragment_profile)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setListeners()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }


    override fun init(rootView: View?) {
        try {
            imageViewProfile = rootView!!.findViewById(R.id.imageViewProfile)
            edittextUserName = rootView!!.findViewById(R.id.edittextUserName)
            edittextEmail = rootView!!.findViewById(R.id.edittextEmail)
            textViewSubjectList = rootView!!.findViewById(R.id.textViewSubjectList)
            edittextMobileNumber = rootView!!.findViewById(R.id.edittextMobileNumber)
            switchCompatIsTeacher = rootView!!.findViewById(R.id.switchCompatIsTeacher)
        }
        catch (e:Exception){
            e.printStackTrace()
        }

        edittextUserName.setText( PreferenceManager.getInstance(activity).userName)
        edittextEmail.setText( PreferenceManager.getInstance(activity).email)
        edittextMobileNumber.setText( PreferenceManager.getInstance(activity).mobileNumber)

        switchCompatIsTeacher.isClickable = false
        if(PreferenceManager.getInstance(activity).isStudent){
            switchCompatIsTeacher.setText("IS Student")
        }
        else{
            switchCompatIsTeacher.setText("Is Teacher")
        }


        val listUser = appDatabase?.getUserOrGroupDao()!!.getAllSubject()
        if (listUser != null && listUser!!.size > 0) run { listSubjects.addAll(listUser!!) }
         var strBf = StringBuffer()
        for(it in listUser){
            strBf.append(it.getName()+"\n")
        }
        textViewSubjectList.setText(strBf.toString())

    }

    override fun setListeners() {

    }

}