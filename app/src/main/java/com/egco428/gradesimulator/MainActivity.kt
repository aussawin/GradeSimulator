package com.egco428.gradesimulator

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 111
    private var gradeMap: HashMap<Course, Double> = hashMapOf()
    private var totalGpa: Double = 0.0
    private var totalCredits: Int = 0
    private var lowPro: Boolean = false
    private var highPro: Boolean = false
    private var honor: Int = 0
    private var socialCredits: Int = 0
    private var languageCredits: Int = 0
    private var scienceMathCredits: Int = 0
    private var coreCredits: Int = 0
    private var specializeCredits: Int = 0
    private var electiveCredits: Int = 0
    private var internCredits: Int = 0

    private var socialCredits_min: Int = 12
    private var languageCredits_min: Int = 12
    private var scienceMathCredits_min: Int = 6
    private var coreCredits_min: Int = 32
    private var specializeCredits_min: Int = 57
    private var electiveCredits_min: Int = 12
    private var internCredits_min: Int = 1

    private var canGetHonor = true
    private var isCooperative = false

    private val honor1st = 3.50..4.00
    private val honor2nd = 3.25..3.49
    private val highProRange = 1.75..1.99
    private val lowProRange = 1.50..1.74

    private var dataSource: CourseDataSource? = null
    private var userCourseList: ArrayList<UserCourse> = arrayListOf()
    private var registedCourse: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataSource = CourseDataSource(this)
        dataSource!!.open()
        dataSource!!.deleteAllData()
//        dataSource!!.close()

        getDataFromSQLite()
    }

    override fun onResume() {
        super.onResume()

        dataSource!!.open()
    }

    override fun onPause() {
        super.onPause()

        dataSource!!.close()
    }

    private fun getDataFromSQLite() {
        dataSource = CourseDataSource(this)
        dataSource!!.open()
        if(dataSource!!.readAllSQLData() != null) {
            registedCourse = arrayListOf()
            val values = dataSource!!.readAllSQLData()

            for (i in values) {
                val gradeStr = gradeDoubleToString(i.grade.toDouble())
                val year = i.groupId / 3 + 1
                val semester = i.groupId % 3 + 1
                val requisite = Requisite(year, semester)
                val tmpCourse = Course(i.courseNo, i.name, i.category, i.credit, i.categoryName, requisite, "")
                userCourseList.add(UserCourse(tmpCourse,gradeStr,i.grade.toDouble(),i.groupId / 3 + 1,i.groupId % 3 + 1))

//                Log.d("position", "$position, ${i.groupId}")
            }
            for(i in userCourseList){
                Log.d("positionReturn", "${i.course} ${i.yearRegisted} ${i.gradeValue}")
            }
            setDetail()
            setAllData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.toCouseRegisted){
            val intent = Intent(this, CourseRegistedActivity::class.java)
            userCourseList.clear()
            getDataFromSQLite()
            val args = Bundle()
            args.putSerializable("tempKey", userCourseList as Serializable)
            intent.putExtra("userCourselist", args)

            startActivityForResult(intent, REQUEST_CODE)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        defaultValue()
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            userCourseList.clear()
            getDataFromSQLite()
        }
    }

    private fun defaultValue(){
        gradeMap = hashMapOf()
        totalGpa = 0.0
        totalCredits = 0
        lowPro = false
        highPro = false
        honor = 0
        socialCredits = 0
        languageCredits = 0
        scienceMathCredits = 0
        coreCredits = 0
        specializeCredits = 0
        electiveCredits = 0
        internCredits = 0
    }

    private fun setDetail(){
        var FState = false
        var sumGpa = 0.0
        totalCredits = 0
        var isWithDraw = false
        Log.d("credits", "${userCourseList.size}")
        val tmp = arrayListOf<UserCourse>()
        tmp.addAll(userCourseList)
        tmp
                .filter { it.grade == "W" }
                .forEach {
                    isWithDraw = true
                    tmp.remove(it)
                }
        for(item in tmp){
            val latest = tmp.filter { it.course.courseNo == item.course.courseNo }
                    .maxBy { item ->
                        (item.yearRegisted-1)*3 + item.semesterRegisted -1 }

            if(item.grade == "F"){
                FState = true
            }
            if(item == latest){
                totalCredits += item.course.credit
                sumGpa += item.gradeValue*item.course.credit
                when (item.course.category) {
                    1 -> coreCredits        += item.course.credit
                    2 -> socialCredits      += item.course.credit
                    3 -> languageCredits    += item.course.credit
                    4 -> scienceMathCredits += item.course.credit
                    5 -> specializeCredits  += item.course.credit
                    6 -> electiveCredits    += item.course.credit
                    7 -> internCredits      += item.course.credit
                    8 -> isCooperative = true
                }
            }
        }
        totalGpa = DecimalFormat("#.00").format(sumGpa/totalCredits).toDouble()
        honor = if (totalGpa in honor1st && canGetHonor && !isWithDraw && !FState) { 1 }
                else if (totalGpa in honor2nd && canGetHonor && !isWithDraw && !FState) { 2 }
                else { -1 }

        highPro = totalGpa in highProRange
        lowPro  = totalGpa in lowProRange
    }

    private fun setAllData(){
        var color = 0
        if (isCooperative){
            setIfIsCooperative()
        }
        else {
            setIfIsNotCooperative()
        }
        valueGPA.text = totalGpa.toString()
        valueCredits.text = totalCredits.toString()
        valueLow.text = if(lowPro) { "ใช่" } else { "ไม่" }
        color = if(lowPro) { Color.RED } else { Color.BLUE }
        valueLow.setTextColor(color)

        valueHigh.text = if(highPro) { "ใช่" } else { "ไม่" }
        color = if(highPro) { Color.RED } else { Color.BLUE }
        valueHigh.setTextColor(color)

        valueHonor.text = when (honor) {
            1 -> "อันดับ 1"
            2 -> "อันดับ 2"
            else -> "ไม่"
        }
        color = when(honor){
            -1 -> Color.RED
            else -> Color.BLUE
        }
        valueHonor.setTextColor(color)

        valueCatagory1_1.text = socialCredits.toString()
        color = if(socialCredits < socialCredits_min) { Color.RED } else { Color.BLUE }
        valueCatagory1_1.setTextColor(color)

        valueCatagory1_2.text = languageCredits.toString()
        color = if(languageCredits < languageCredits_min) { Color.RED } else { Color.BLUE }
        valueCatagory1_2.setTextColor(color)

        valueCatagory1_3.text = scienceMathCredits.toString()
        color = if(scienceMathCredits < scienceMathCredits_min) { Color.RED } else { Color.BLUE }
        valueCatagory1_3.setTextColor(color)

        valueCatagory2_1.text = coreCredits.toString()
        color = if(coreCredits < coreCredits_min) { Color.RED } else { Color.BLUE }
        valueCatagory2_1.setTextColor(color)

        valueCatagory2_2.text = specializeCredits.toString()
        color = if(specializeCredits < specializeCredits_min) { Color.RED } else { Color.BLUE }
        valueCatagory2_2.setTextColor(color)

        valueCatagory2_3.text = electiveCredits.toString()
        color = if(electiveCredits < electiveCredits_min) { Color.RED } else { Color.BLUE }
        valueCatagory2_3.setTextColor(color)

        valueCategory3.text   = internCredits.toString()
        color = if(internCredits < internCredits_min) { Color.RED } else { Color.BLUE }
        valueCategory3.setTextColor(color)

        valueCategory4.text   = "_"
    }

    private fun setIfIsCooperative(){
        specializeCredits_min = 56
        mainText5.text = "/$specializeCredits_min"
        electiveCredits_min = 6
        mainText6.text = "/$electiveCredits_min"
        internCredits_min = 0
        mainText7.text = "/$internCredits_min"
    }

    private fun setIfIsNotCooperative(){
        specializeCredits_min = 57
        mainText5.text = "/$specializeCredits_min"
        electiveCredits_min = 12
        mainText6.text = "/$electiveCredits_min"
        internCredits_min = 1
        mainText7.text = "/$internCredits_min"
    }

    private fun gradeDoubleToString(grade: Double): String{
        return when (grade) {
            4.0  -> "A"
            3.5  -> "B+"
            3.0  -> "B"
            2.5  -> "C+"
            2.0  -> "C"
            1.5  -> "D+"
            1.0  -> "D"
            0.0  -> "F"
            else -> "W"
        }
    }
}
