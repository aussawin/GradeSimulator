package com.egco428.gradesimulator

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 111
    private lateinit var gradeMap: HashMap<Course, String>
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
            val tempMap = data.extras.get("gradeMap") as HashMap<Course, String>

            gradeMap = tempMap

            for (i in gradeMap) {
                Log.d("gradeMap", i.key.courseNo + ": " + i.value)
            }
        }
    }

    private fun setDetail(){
        var sumGpa: Int = 0
        for (i in gradeMap){
            totalCredits += i.key.credit

        }
    }
}
