package com.egco428.gradesimulator

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.graphics.Typeface
import android.util.Log
import android.widget.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CustomExpandableListAdapter(private val context: Context,
                                    private val expandableListTitle: List<String>,
                                    private val expandableListDetail: HashMap<String, List<UserCourse>> = HashMap(),
                                    private val courseRegisted: CourseRegistedActivity)
    : BaseExpandableListAdapter() {
    var addSubjectBtn: ImageButton? = null
//    private val gradeMap: HashMap<Course, Double> = hashMapOf()

    override fun getChild(listPosition: Int, expandedListPosition: Int): UserCourse {
        return expandableListDetail[expandableListTitle[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getChildView(listPosition: Int, expandedListPosition: Int, isLastChild: Boolean, convertView: View?, ViewGroup: ViewGroup?): View {

        val subject = getChild(listPosition, expandedListPosition)
        val subjCode: String = subject.course.courseNo
        val subjName: String = subject.course.name

        var tempConvertView = convertView

        if (tempConvertView == null){
            val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            tempConvertView = layoutInflater.inflate(R.layout.list_item, null)

        }

        val graded = tempConvertView!!.findViewById<View>(R.id.gradeDropDown) as Spinner
        val gradedText = tempConvertView.findViewById<View>(R.id.gradeText) as TextView

        graded.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) { }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                gradedText.text = graded.selectedItem.toString()
                val gradeStr = gradedText.text.toString()
                val gradeValue = when (gradeStr) {
                    "A"  -> 4.0
                    "B+" -> 3.5
                    "B"  -> 3.0
                    "C+" -> 2.5
                    "C"  -> 2.0
                    "D+" -> 1.5
                    "D"  -> 1.0
                    "F"  -> 0.0
                    "S"  -> -0.5
                    else -> -1.0
                }
                courseRegisted.setGrade(subject.course,gradeValue,gradeStr,listPosition / 3 +1,listPosition % 3 +1)
//                gradeMap.put(subject.course, gradeValue)
//
//                courseRegisted.getGradeMapMethod(gradeMap)

//                Log.d("Dropdown","${graded.selectedItemPosition}")
            }

        }
//        graded.isSelected = when()
        val subjCodeView = tempConvertView.findViewById<View>(R.id.subjectCode) as TextView
        val subjNameView = tempConvertView.findViewById<View>(R.id.subjectName) as TextView
        subjNameView.text = subjName
        subjCodeView.text = subjCode
//        gradedText.text = subject.grade
        var index = Math.abs(((subject.gradeValue-4)*2).toInt())
        if(subject.grade.equals("F") || subject.grade == "S" || subject.grade == "W"){
            index -= 1
        }
        Log.d("spinner setting","$subjCode ${subject.gradeValue} $index")
        graded.setSelection(index )
        return tempConvertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return expandableListDetail[expandableListTitle[listPosition]]!!.size
    }

    override fun getGroup(listPosition: Int): Any {
        return expandableListTitle[listPosition]
    }

    override fun getGroupCount(): Int {
        return expandableListTitle.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getGroupView(listPosition: Int //Year and semester
                              , isExpanded: Boolean
                              , convertView: View?
                              , parent: ViewGroup?): View {
        var tempConvertView = convertView
        val listTitle: String = getGroup(listPosition) as String

        if (tempConvertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            tempConvertView = layoutInflater.inflate(R.layout.list_group, null)
        }

        val listTitleTextView = tempConvertView!!.findViewById<View>(R.id.listTitle) as TextView
        addSubjectBtn = tempConvertView.findViewById<View>(R.id.addSubject) as ImageButton
        addSubjectBtn!!.isFocusable = false
        Log.d("Size","${expandableListDetail.size}")
        addSubjectBtn!!.setOnClickListener {
            val courseForIntent = if (expandableListDetail[expandableListTitle[listPosition]]!!.isEmpty()) {
                arrayListOf()
            } else {
                userCourseToCourse(expandableListDetail[expandableListTitle[listPosition]]!!)
            }
            val positionForIntent = getGroupId(listPosition).toInt()

            courseRegisted.dataTransferMethod(positionForIntent, courseForIntent)
        }

        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle

        return tempConvertView
    }

    private fun userCourseToCourse(usercourselist: List<UserCourse>): ArrayList<Course> {
        val courseArray: ArrayList<Course> = arrayListOf()
        for(i in usercourselist){
            courseArray.add(i.course)
        }
        return courseArray
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }
}