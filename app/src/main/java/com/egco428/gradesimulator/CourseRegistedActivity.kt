package com.egco428.gradesimulator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.ExpandableListAdapter
import android.widget.ListAdapter
import kotlinx.android.synthetic.main.activity_course_registed.*


class CourseRegistedActivity : AppCompatActivity() {

//    var expandableListView: ExpandableListView = null
    var expandableListAdapter: ExpandableListAdapter? = null
    var expandableListTitle: List<String>? = null
    var expandableListDetail: HashMap<String, List<Course>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_registed)
        val MyexpandableListView = expandableListView
        expandableListDetail = ExpandableListDataPump().getData()
        expandableListTitle = expandableListDetail!!.keys.toList()
        expandableListAdapter = CustomExpandableListAdapter(this, expandableListTitle!!, expandableListDetail!!)
//        expandableListView.setAdapter(expandableListAdapter)
        MyexpandableListView.setAdapter(expandableListAdapter)



    }
}
