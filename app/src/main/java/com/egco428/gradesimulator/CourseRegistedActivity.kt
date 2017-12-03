package com.egco428.gradesimulator

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ExpandableListView
import android.widget.ExpandableListAdapter
import android.widget.ListAdapter
import kotlinx.android.synthetic.main.activity_course_registed.*

class CourseRegistedActivity : AppCompatActivity() {

//    var expandableListView: ExpandableListView = null
    private var expandableListAdapter: ExpandableListAdapter? = null
    private var expandableListTitle: List<String>? = null
    private var expandableListDetail: HashMap<String, List<Course>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_registed)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val myExpandableListView = expandableListView
        expandableListDetail = ExpandableListDataPump().getData()
        expandableListTitle = expandableListDetail!!.keys.sorted().toList()
        for (i in 0..(expandableListTitle!!.size-1))
            Log.e("Test Name", expandableListTitle!![i])
        expandableListAdapter = CustomExpandableListAdapter(this, expandableListTitle!!, expandableListDetail!!)
//        expandableListView.setAdapter(expandableListAdapter)
        myExpandableListView.setAdapter(expandableListAdapter)



    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_course_registed, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.addCourse) {
            val intentToCourseList = Intent(this,CourseListActivity::class.java)
            startActivity(intentToCourseList)
            return true
        }
        else if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
