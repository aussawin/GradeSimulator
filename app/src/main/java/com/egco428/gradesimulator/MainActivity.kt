package com.egco428.gradesimulator

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 111
    private lateinit var gradeMap: HashMap<Course, Double>
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

    private var canGetHonor = true
    private var isCooperative = false

    private val honor1st = 3.50..4.00
    private val honor2nd = 3.25..3.49
    private val highProRange = 1.75..1.99
    private val lowProRange = 1.50..1.74

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.toCouseRegisted){
            val intent = Intent(this, CourseRegistedActivity::class.java)

            startActivityForResult(intent, REQUEST_CODE)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data!!.extras.get("gradeMap") != null){
            val tempMap = data.extras.get("gradeMap") as HashMap<Course, Double>

            gradeMap = tempMap

            for (i in gradeMap) {
                Log.d("gradeMap", i.key.courseNo + ": " + i.value)
            }
            setDetail()
            setAllData()
        }

    }

    private fun setDetail(){
        var sumGpa = 0.0
        for (i in gradeMap){
            val course = i.key
            val grade = i.value
            totalCredits += course.credit
            sumGpa += grade*course.credit

            if (grade == 0.0 || grade == -1.0){
                canGetHonor = false
            }

            when (course.category) {
                1 -> coreCredits        += course.credit
                2 -> socialCredits      += course.credit
                3 -> languageCredits    += course.credit
                4 -> scienceMathCredits += course.credit
                5 -> specializeCredits  += course.credit
                6 -> electiveCredits    += course.credit
                7 -> internCredits      += course.credit
                8 -> isCooperative = true
            }
        }
        totalGpa = DecimalFormat("#.00").format(sumGpa/totalCredits).toDouble()
        honor = if      (totalGpa in honor1st && canGetHonor) { 1 }
                else if (totalGpa in honor2nd && canGetHonor) { 2 }
                else { -1 }

        highPro = totalGpa in highProRange
        lowPro  = totalGpa in lowProRange
    }

    private fun setAllData(){
        valueGPA.text = totalGpa.toString()
        valueCredits.text = totalCredits.toString()
        valueLow.text = if(lowPro) { "ใช่" } else { "ไม่" }
        valueHigh.text = if(highPro) { "ใช่" } else { "ไม่" }
        valueHonor.text = when (honor) {
            1 -> "อันดับ 1"
            2 -> "อันดับ 2"
            else -> "ไม่"
        }
        valueCatagory1_1.text = socialCredits.toString()
        valueCatagory1_2.text = languageCredits.toString()
        valueCatagory1_3.text = scienceMathCredits.toString()
        valueCatagory2_1.text = coreCredits.toString()
        valueCatagory2_2.text = specializeCredits.toString()
        valueCatagory2_3.text = electiveCredits.toString()
        valueCategory3.text   = internCredits.toString()
        valueCategory4.text   = "_"
    }
}
