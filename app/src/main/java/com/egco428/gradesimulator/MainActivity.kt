package com.egco428.gradesimulator

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
    private var freeCourseCredits: Int = 0

    private var socialCredits_min: Int = 12
    private var languageCredits_min: Int = 12
    private var scienceMathCredits_min: Int = 6
    private var coreCredits_min: Int = 32
    private var specializeCredits_min: Int = 57
    private var electiveCredits_min: Int = 12
    private var internCredits_min: Int = 1
    private var freeCourseCredits_min:Int = 6
    private var canGetHonor = true
    private var isCooperative = false

    private val honor1st = 3.50..4.00
    private val honor2nd = 3.25..3.49
    private val highProRange = 1.75..1.99
    private val lowProRange = 1.50..1.74

    private var dataSource: CourseDataSource? = null
    private var userCourseList: ArrayList<UserCourse> = arrayListOf()
    private var registedCourse: ArrayList<String> = arrayListOf()
    private var riskedCourse: ArrayList<String> = arrayListOf() //วิชาที่เสี่ยงจะ 3 ไม้
    private var normalStatus = true //สถานะนักศึกษาปกติ
    private var probationTimes = 0
    private var noGradeS = 0


    private val lowProbationTime = 2
    private val highProbationTime = 4

    private val lowProbationGrade = 1.75
    private val highProbationGrade = 2.00
    private val retireGrade = 1.5

    private var greenColor = Color.parseColor("#04B431")
    private var eachSemesterGrade = HashMap<Int,Double>()
    private var eachSemesterSumGrade = HashMap<Int,Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataSource = CourseDataSource(this)
        dataSource!!.open()
//        dataSource!!.close()
        getDataFromSQLite()
        exclamation.setOnClickListener {
            val dialog = MainActivity.ExclamationDialog()
            val ft = fragmentManager.beginTransaction()
            val prev = fragmentManager.findFragmentByTag("dialog")
            val args = Bundle()

            args.putStringArray("riskedCourses", riskedCourse.toTypedArray())
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialog.arguments = args
            dialog.show(ft, "dialog")
        }

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
        noGradeS = 0
        dataSource = CourseDataSource(this)
        dataSource!!.open()
        riskedCourse.clear()
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
            defaultValue()
            riskedCourse.clear()
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
        noGradeS = 0
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
        freeCourseCredits = 0
        eachSemesterSumGrade.clear()
        eachSemesterGrade.clear()
        probationTimes = 0
    }

    private fun setDetail(){
        normalStatus = true
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
            val registedTimes = userCourseList.filter { it.course.courseNo == item.course.courseNo }.size

            if(registedTimes >= 2 && latest != null && latest.gradeValue < 1){
                Log.d("Latest","${latest.gradeValue}")
                if(!riskedCourse.any { it == latest.course.courseNo }) {
                    Log.d("Add","Add")
                    riskedCourse.add(latest.course.courseNo)
                    if (registedTimes > 2){
                        normalStatus = false
                    }
                }

            }



            if(item.grade == "F"){
                FState = true
            }
            if(item == latest){
                if (item.grade!="S"){
                    totalCredits += item.course.credit
                    sumGpa += item.gradeValue*item.course.credit
                }
                else{
                    noGradeS++
                }
                when (item.course.category) {
                    1 -> {
                        if(coreCredits < coreCredits_min){
                           coreCredits  += item.course.credit
                        }
                        else {
                            freeCourseCredits += coreCredits_min - coreCredits+item.course.credit
                        }
                    }
                    2 -> {
                        if(socialCredits < socialCredits_min){
                            socialCredits  += item.course.credit
                        }
                        else {
                            freeCourseCredits += socialCredits_min - socialCredits+item.course.credit
                        }
                    }
                    3 ->  {
                        if(languageCredits < languageCredits_min){
                            languageCredits  += item.course.credit
                        }
                        else {
                            freeCourseCredits += languageCredits_min - languageCredits+item.course.credit
                        }
                    }
                    4 -> {
                        if(scienceMathCredits < scienceMathCredits_min){
                            scienceMathCredits  += item.course.credit
                        }
                        else {
                            freeCourseCredits += scienceMathCredits_min - scienceMathCredits+item.course.credit
                        }
                    }
                    5 -> {
                        if(specializeCredits < specializeCredits_min){
                            specializeCredits  += item.course.credit
                        }
                        else {
                            freeCourseCredits += specializeCredits_min - specializeCredits+item.course.credit
                        }
                    }
                    6 -> {
                        if(electiveCredits < electiveCredits_min){
                            electiveCredits  += item.course.credit
                        }
                        else {
                            freeCourseCredits += electiveCredits_min - electiveCredits+item.course.credit
                        }
                    }
                    7 ->{
                        if(internCredits < internCredits_min){
                            internCredits  += item.course.credit
                        }
                        else {
                            freeCourseCredits += internCredits_min - internCredits+item.course.credit
                        }
                    }
                    8 -> isCooperative = true
                }
            }
        }
        exclamation.visibility = if(riskedCourse.size > 0){ View.VISIBLE }
                                    else { View.GONE}

        totalGpa = DecimalFormat("#.00").format(sumGpa/totalCredits).toDouble()
        honor = if (totalGpa in honor1st && canGetHonor && !isWithDraw && !FState) { 1 }
                else if (totalGpa in honor2nd && canGetHonor && !isWithDraw && !FState) { 2 }
                else { -1 }

        highPro = totalGpa in highProRange
        lowPro  = totalGpa in lowProRange
        if (userCourseList==null){
            setProbation()
        }

    }

    private fun getGradebySemester(courseArray: List<UserCourse>, year : Int,semester: Int): Double? {
        val tmp = ArrayList(courseArray.filter { it.yearRegisted <= year && it.semesterRegisted <= semester })
        if (tmp.isEmpty()){
            return null
        }
        tmp.removeIf { it.yearRegisted != tmp.filter { it1 -> it1.course.courseNo == it.course.courseNo }.maxBy { it2 -> (it2.yearRegisted-1)*3 + it2.semesterRegisted -1}!!.yearRegisted
                        || it.semesterRegisted != tmp.filter { it1 -> it1.course.courseNo == it.course.courseNo }.maxBy { it2 -> (it2.yearRegisted-1)*3 + it2.semesterRegisted -1}!!.semesterRegisted}

        return tmp.sumByDouble { it.gradeValue*it.course.credit } / tmp.sumBy { it.course.credit }
    }

    private fun setProbation(){
        Log.d("setProbation", "setProbation start")
        val normalSemesterCourses = userCourseList.filter { it.semesterRegisted < 3 && it.grade.toUpperCase() != "W" }.sortedBy { it.yearRegisted }.sortedBy { it.semesterRegisted } //getAllSemesters except summer semesters
        Log.d("SSSS","XXXXXXXXXXXXXXX" + normalSemesterCourses)
        val maxPositionCourse = normalSemesterCourses.maxBy { (it.yearRegisted-1)*3 + it.semesterRegisted -1 }!!
        Log.d("SSSS","XXXXXXXXXXXXXXX" + maxPositionCourse)
        val maxPosition = (maxPositionCourse.yearRegisted-1)*3 + maxPositionCourse.semesterRegisted -1

        for (i in 0..maxPosition) {
            val year = i / 3 +1
            val semester = i % 3 +1
            val gradeAtsemester = getGradebySemester(normalSemesterCourses,year,semester)
            if(gradeAtsemester != null ){

                val avgGrade = gradeAtsemester

                if (avgGrade < highProbationGrade){
                    probationTimes++
                    var remain = 0
                    if (avgGrade < retireGrade){
                        normalStatus = false
                        Log.d("retire", "$i $avgGrade")
                        valueProbation.text = "ไม่"
                           valueRemaining.text = "-"
                    }
                    else if (avgGrade < lowProbationGrade){
                        normalStatus = probationTimes <= lowProbationTime
                        valueProbation.text = "โปรต่ำ"
                        remain = if (lowProbationTime-probationTimes >= 0){
                            lowProbationTime-probationTimes
                        }else {
                            0
                        }
                        valueRemaining.text = "$remain"
                    }
                    else {
                        normalStatus = probationTimes <= highProbationTime
                        valueProbation.text = "โปรสูง"
                        remain = if (highProbationTime-probationTimes >= 0){
                            highProbationTime-probationTimes
                        }else {
                            0
                        }
                        valueRemaining.text = "$remain"
                    }
                }
                else{
                    probationTimes = 0
                    valueProbation.text = "ไม่"
                    valueRemaining.text = "-"
                }
            }
//                Log.d("probationTime","${probationTimes} ${i} ${eachSemesterSumGrade[i]}")
        }
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
        valueCredits.text = (totalCredits+noGradeS).toString()
//        valueLow.text = if(lowPro) { "ใช่" } else { "ไม่" }
//        color = if(lowPro) { Color.RED } else { greenColor }
//        valueLow.setTextColor(color)

//        valueRemaining.text = if(highPro) { "ใช่" } else { "ไม่" }
//        color = if(highPro) { Color.RED } else { greenColor }
//        valueRemaining.setTextColor(color)

        color = when {
            totalGpa < 2.00 -> Color.RED
            totalGpa < 3.00 -> Color.parseColor("#FFBF00")
            else -> greenColor
        }
        valueGPA.setTextColor(color)

        valueHonor.text = when (honor) {
            1 -> "อันดับ 1"
            2 -> "อันดับ 2"
            else -> "ไม่"
        }
        color = when(honor){
            -1 -> Color.RED
            else -> greenColor
        }
        valueHonor.setTextColor(color)

        valueCatagory1_1.text = socialCredits.toString()
        color = if(socialCredits < socialCredits_min) { Color.RED } else { greenColor }
        valueCatagory1_1.setTextColor(color)

        valueCatagory1_2.text = languageCredits.toString()
        color = if(languageCredits < languageCredits_min) { Color.RED } else { greenColor }
        valueCatagory1_2.setTextColor(color)

        valueCatagory1_3.text = scienceMathCredits.toString()
        color = if(scienceMathCredits < scienceMathCredits_min) { Color.RED } else { greenColor }
        valueCatagory1_3.setTextColor(color)

        valueCatagory2_1.text = coreCredits.toString()
        color = if(coreCredits < coreCredits_min) { Color.RED } else { greenColor }
        valueCatagory2_1.setTextColor(color)

        valueCatagory2_2.text = specializeCredits.toString()
        color = if(specializeCredits < specializeCredits_min) { Color.RED } else { greenColor }
        valueCatagory2_2.setTextColor(color)

        valueCatagory2_3.text = electiveCredits.toString()
        color = if(electiveCredits < electiveCredits_min) { Color.RED } else { greenColor }
        valueCatagory2_3.setTextColor(color)

        valueCategory3.text   = internCredits.toString()
        color = if(internCredits < internCredits_min) { Color.RED } else { greenColor }
        valueCategory3.setTextColor(color)

        valueCategory4.text   = freeCourseCredits.toString()
        color = if(freeCourseCredits < freeCourseCredits_min) { Color.RED } else { greenColor }
        valueCategory4.setTextColor(color)


        if(normalStatus){
            valueStatus.text = "ปกติ"
            valueStatus.setTextColor(greenColor)
        }
        else{
            valueStatus.text = "พ้นสภาพนักศึกษา"
            valueStatus.setTextColor(Color.RED)
        }

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
            -0.5  -> "S"
            else -> "W"
        }
    }

    class ExclamationDialog: DialogFragment() {
        private lateinit var riskedCourseSet: Array<String>
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            Log.d("Check","Created")
            val builder = AlertDialog.Builder(activity)
            riskedCourseSet = arguments.getStringArray("riskedCourses")

            builder.setTitle("รายวิชาที่ไม่ผ่านเกิน 2 ครั้ง")
                    .setItems(riskedCourseSet,null)
            return builder.create()
        }
    }
}
