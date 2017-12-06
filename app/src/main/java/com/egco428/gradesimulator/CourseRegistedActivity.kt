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
import java.time.LocalDate

class CourseRegistedActivity : AppCompatActivity() {

    private var expandableListAdapter: ExpandableListAdapter? = null
    private var customExpandableListAdapter: CustomExpandableListAdapter? = null
    private var expandableListTitle: List<String>? = null
    private var expandableListDetail: HashMap<String, List<UserCourse>>? = hashMapOf()

    private val REQUEST_CODE = 1111

    private var position: Int = 0 //(Year-1)*3 + Semester

    private var gradeMap: HashMap<Course, Double> = hashMapOf()
    private var dataSource: CourseDataSource? = null
    private var userCourseList: ArrayList<UserCourse> = arrayListOf()
    private val maxTerm = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_registed)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        if (intent.extras.get("userCourselist") != null) {
            val temp = intent.getBundleExtra("userCourselist")
            if (temp.getSerializable("tempKey") != null) {

                userCourseList = temp.getSerializable("tempKey") as ArrayList<UserCourse>
                val objMax = userCourseList.maxBy { item ->
                    (item.yearRegisted-1)*3 + item.semesterRegisted -1
                }
                var positionMax = 0
                if (objMax != null) {
                    positionMax = (objMax.yearRegisted-1)*3 + objMax.semesterRegisted -1
                    Log.d("Max", "${objMax.yearRegisted} ${objMax.semesterRegisted} ${positionMax}")
                }

                val tmpExpandable = hashMapOf<String, ArrayList<UserCourse>>()
                if (userCourseList.isNotEmpty()) {
                    for(i in 0..positionMax){
                        val year = i / 3 +1
                        val semester = i % 3 +1
                        val key = "Year ${year}: Semester ${semester}"
                        val data = userCourseList.filter { it.yearRegisted == year && it.semesterRegisted == semester } as ArrayList<UserCourse>

//                        val data = arrayListOf<UserCourse>(userCourseList[0])
//                        Log.d("Loop","${data[0].course}")
//                        tmpExpandable.put(key, data)
                        expandableListDetail!!.put(key, data)
                    }
//                    for (item in userCourseList) {
//
//
//                        val key = "Year ${item.yearRegisted}: Semester ${item.semesterRegisted}"
//                        Log.d("key 1", "${item.gradeValue}")
//                        if (tmpExpandable[key] == null) {
//                            tmpExpandable.put(key, arrayListOf())
//                        }
//                        tmpExpandable[key]!!.add(item)
//                    }
//                    for (item in tmpExpandable) {
//                        Log.d("key 2", item.key)
//
//                    }
                }
            }

            reloadData()
        }
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

            args.putSerializable("selected_courselist", data as Serializable)

            intentToCourseList.putExtra("arrayList", args)
        }

        intentToCourseList.putExtra("position", position)

        startActivityForResult(intentToCourseList, REQUEST_CODE)
    }

    fun getGradeMapMethod(data: HashMap<Course, Double>) {
        gradeMap = data
    }

    fun setGrade(course: Course, gradeValue: Double, gradeStr: String, year: Int, semester: Int){
        val userCourseSelected = userCourseList.find { it.course.courseNo == course.courseNo && it.yearRegisted == year && it.semesterRegisted == semester }
        if(userCourseSelected != null) {
            userCourseList.find { it.course.courseNo == course.courseNo && it.yearRegisted == year && it.semesterRegisted == semester }!!.grade = gradeStr
            userCourseList.find { it.course.courseNo == course.courseNo && it.yearRegisted == year && it.semesterRegisted == semester } !!.gradeValue = gradeValue
        }
    }

    private fun courseToUserCourse(courselist: ArrayList<Course>,year: Int,semester: Int): ArrayList<UserCourse>{
        val userCourselist = arrayListOf<UserCourse>()

        for (item in courselist){
            if(userCourseList.find { it.course.courseNo == item.courseNo && it.yearRegisted == year && it.semesterRegisted == semester} != null) {
                val userCourse = userCourseList.find { it.course.courseNo == item.courseNo && it.yearRegisted == year && it.semesterRegisted == semester}
                userCourselist.add(userCourse!!)
            }
            else {
                userCourselist.add(UserCourse(item, "A", 4.00, year, semester))
            }
        }
        return  userCourselist
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data!!.getBundleExtra("courseObject") != null) {
            val obj: ArrayList<Course>
            val temp = data.getBundleExtra("courseObject")

            obj = temp.getSerializable("tempKey") as ArrayList<Course>
            position = data.extras.getInt("returnPosition")
            val userCourseObj = courseToUserCourse(obj, (position / 3 + 1),position % 3 + 1)
//            Log.d("Position","$position")
            expandableListDetail!!["Year ${position / 3 + 1}: Semester ${position % 3 + 1}"] = userCourseObj

            userCourseList
                    .filter { it.yearRegisted == position / 3 + 1 && it.semesterRegisted == position % 3 + 1 }
                    .forEach { userCourseList.remove(it) }

            userCourseList.addAll(userCourseObj)

            reloadData()
            expandableListView.expandGroup(position)
            for(i in userCourseList){
                Log.d("course","${i.course} ${i.gradeValue} ${i.grade}")
            }
        }
    }

    private fun writeDataToDatabase(){
        dataSource = CourseDataSource(this)
        dataSource!!.open()
        dataSource!!.deleteAllData()

        Log.d("writing data","start")
        for (i in userCourseList ) {
            Log.d("writing data","${i.course.courseNo} ${i.gradeValue} ${i.grade}")
            dataSource!!.createSubject(
                    i.course.courseNo,
                    i.course.name,
                    i.course.category,
                    i.course.credit,
                    i.course.categoryName,
                    (i.yearRegisted-1) * 3 + i.semesterRegisted - 1,
                    i.gradeValue.toString())
        }


        dataSource!!.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_course_registed, menu)
        return true
    }
    private fun positionToYearSemester(position: Int): String{
        return "Year ${position / 3 + 1}: Semester ${position % 3 + 1}"
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            var position = customExpandableListAdapter!!.groupCount
            when {
                item.itemId == android.R.id.home -> {
                    finish()
                    return true
                }
                item.itemId == R.id.addTerm -> {
                    if (position <= maxTerm) {
                        val key = positionToYearSemester(position)
                        expandableListDetail!!.put(key, listOf())
                    }
                }
                item.itemId == R.id.removeTerm -> {
                    position--
                    val key = positionToYearSemester(position)
                    expandableListDetail!!.remove(key)
                }
                item.itemId == R.id.removeAll -> {
                    expandableListDetail = hashMapOf()
                }
                item.itemId == R.id.saveData -> {
                    val intent = Intent(this, MainActivity::class.java)
//
////                    intent.putExtra("gradeMap", gradeMap)
//                    if (userCourseList.isNotEmpty()) {
//                        val args = Bundle()
//
//                        args.putSerializable("courselist", userCourseList as Serializable)
//
//                        intent.putExtra("serialized_courselist", args)
//                    }
//
////                    intent.putExtra("position", position)
//                    for (i in userCourseList){
//                        Log.d("userCourse","${i.course.courseNo}")
//                    }
                    setResult(Activity.RESULT_OK, intent)
                    writeDataToDatabase()
                    finish()
                }
            }

            reloadData()
        }
        return super.onOptionsItemSelected(item)
    }
}