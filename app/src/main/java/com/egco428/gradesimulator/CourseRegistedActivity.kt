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
    private var expandableListDetail: HashMap<String, List<Course>>? = hashMapOf()
    private val REQUEST_CODE = 1111
    private var position: Int = 0
    private var gradeMap: HashMap<Course, Double> = hashMapOf()
    private var datasource: CourseDataSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_registed)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

//        if(datasource != null) {
//            val values = datasource!!.allSubjectInSemester
//        }

        reloadData()
    }

    private fun reloadData() {
        expandableListTitle = expandableListDetail!!.keys.sorted().toList()
        customExpandableListAdapter = CustomExpandableListAdapter(this, expandableListTitle!!, expandableListDetail!!,this)
        expandableListAdapter = customExpandableListAdapter

        expandableListView.setAdapter(expandableListAdapter)
    }

    fun dataTransferMethod(position: Int, data: ArrayList<Course>){
        val intentToCourseList = Intent(this,CourseListActivity::class.java)

        if (data != null) {
            val args = Bundle()

            args.putSerializable("arrayList_2", data as Serializable)

            intentToCourseList.putExtra("arrayList", args)
        }

        intentToCourseList.putExtra("position", position)

        startActivityForResult(intentToCourseList, REQUEST_CODE)
    }

    fun getGradeMapMethod(data: HashMap<Course, Double>) {
        gradeMap = data
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data!!.getBundleExtra("courseObject") != null) {
            val obj: ArrayList<Course>
            val temp = data.getBundleExtra("courseObject")
            obj = temp.getSerializable("tempKey") as ArrayList<Course>
            position = data.extras.getInt("returnPosition")

            datasource = CourseDataSource(this, position)
            datasource!!.open()

            for (i in obj){
                datasource!!.createSubject(
                        i.courseNo,
                        i.name,
                        i.category,
                        i.credit,
                        i.categoryName,
                        position,
                        gradeMap.values.toString())
            }

            val values = datasource!!.allSubjectInSemester

            expandableListDetail!!["Year ${position / 3 + 1}: Semester ${position % 3 + 1}"] = obj
            reloadData()
            expandableListView.expandGroup(position)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_course_registed, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            var count = customExpandableListAdapter!!.groupCount
            when {
                item.itemId == android.R.id.home -> {
                    finish()
                    return true
                }
                item.itemId == R.id.addTerm -> {
                    if (count <= 11) {
                        val key = "Year ${count / 3 + 1}: Semester ${count % 3 + 1}"
                        expandableListDetail!!.put(key, listOf())
                    }
                }
                item.itemId == R.id.removeTerm -> {
                    count--
                    val key = "Year ${count / 3 + 1}: Semester ${count % 3 + 1}"
                    expandableListDetail!!.remove(key)
                }
                item.itemId == R.id.removeAll -> {
                    expandableListDetail = hashMapOf()
                }
                item.itemId == R.id.saveData -> {
                    val intent = Intent(this, MainActivity::class.java)

                    intent.putExtra("gradeMap", gradeMap)

                    setResult(Activity.RESULT_OK, intent)

                    finish()
                }
            }

            reloadData()
        }
        return super.onOptionsItemSelected(item)
    }
}
