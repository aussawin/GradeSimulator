package com.egco428.gradesimulator

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ExpandableListAdapter
import kotlinx.android.synthetic.main.activity_course_registed.*
import java.io.Serializable

class CourseRegistedActivity : AppCompatActivity() {
    private var expandableListAdapter: ExpandableListAdapter? = null
    private var customExpandableListAdapter: CustomExpandableListAdapter? = null
    private var expandableListTitle: List<String>? = null
    private var expandableListDetail: HashMap<String, List<Course>>? = null
    private val REQUEST_CODE = 1111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_registed)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val myExpandableListView = expandableListView

        expandableListDetail = ExpandableListDataPump().getData()
        expandableListTitle = expandableListDetail!!.keys.sorted().toList()
        customExpandableListAdapter = CustomExpandableListAdapter(this, expandableListTitle!!, expandableListDetail!!,this)
        expandableListAdapter = customExpandableListAdapter

        myExpandableListView.setAdapter(expandableListAdapter)
    }
    fun dataTransferMethod(data: Array<Course>){
        val intentToCourseList = Intent(this,CourseListActivity::class.java)
        val args = Bundle()
        args.putSerializable("arrayList", data as Serializable)
        intentToCourseList.putExtra("arrayList_2", args)

        startActivityForResult(intentToCourseList, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data!!.getBundleExtra("courseObject") != null){
            val temp = data.getBundleExtra("courseObject")
            val obj = temp.getSerializable("tempKey") as ArrayList<Course>
            for (i in obj) {
                Log.d("object", i.courseNo)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_course_registed, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.addCourse) {
            val intentToCourseList = Intent(this,CourseListActivity::class.java)
            Log.d("Activity", "start! >> CourseList Activity")
            startActivityForResult(intentToCourseList, REQUEST_CODE)
            return true
        }
        else if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}
