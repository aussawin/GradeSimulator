package com.egco428.gradesimulator

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.graphics.Typeface
import android.widget.*

/**
 * Created by Aussawin on 11/1/2017 AD.
 */
class CustomExpandableListAdapter(private val context: Context,
                                  private val expandableListTitle: List<String>,
                                  private val expandableListDetail: HashMap<String, List<Course>> = HashMap())
    : BaseExpandableListAdapter() {

    override fun getChild(listPosition: Int, expandedListPosition: Int): Course {
        return expandableListDetail[expandableListTitle[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(listPosition: Int, expandedListPosition: Int, isLastChild: Boolean, convertView: View?, ViewGroup: ViewGroup?): View {
        val subjCode: String = getChild(listPosition, expandedListPosition).courseNo
        val subjName: String = getChild(listPosition, expandedListPosition).name
        var tempConvertView = convertView

        if (tempConvertView == null){
            val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            tempConvertView = layoutInflater.inflate(R.layout.list_item, null)

        }

//        val graded = tempConvertView!!.findViewById<View>(R.id.gradeDropDown) as Spinner
//        val gradeAll = arrayOf("A","B","C","D","E","F")
//        graded.adapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, gradeAll)
//        graded.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//        }
        val subjCodeView = tempConvertView!!.findViewById<View>(R.id.subjectCode) as TextView
        val subjNameView = tempConvertView!!.findViewById<View>(R.id.subjectName) as TextView
        subjNameView.text = subjName
        subjCodeView.text = subjCode
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
    override fun getGroupView(listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var tempConvertView = convertView
        val listTitle: String = getGroup(listPosition) as String
        if (tempConvertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            tempConvertView = layoutInflater.inflate(R.layout.list_group, null)
        }
        val listTitleTextView = tempConvertView!!.findViewById<View>(R.id.listTitle) as TextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle
        return tempConvertView
    }

    override fun hasStableIds(): Boolean {
        return false;
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true;
    }

}