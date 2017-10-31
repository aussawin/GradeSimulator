package com.egco428.gradesimulator

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.course_register.view.*
import kotlinx.android.synthetic.main.each_semester.view.*

/**
 * Created by Onewdivide on 1/11/2560.
 */
class CourseRegisterActivity : AppCompatActivity() {

    lateinit var EachSubject : RecyclerView
    var subjectCode:MutableList<String> = ArrayList()
    var subjectName:MutableList<String> = ArrayList()
    lateinit var grade : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_register)

        EachSubject = findViewById(R.id.EachSemester)
        EachSubject.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        EachSubject.adapter = subjectAdapter(subjectCode,subjectName,this)

        grade = findViewById(R.id.grade)
        var gradeAll = arrayOf("A","B","C","D","E","F")
        grade.adapter = ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,gradeAll)
        grade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }

//        val subject = ArrayList<Subject>()
//        subject.add(Subject("SCMA 115","Calculus"))
//        subject.add(Subject("SCPY 110","PhysicsLaboratory1"))
//        subject.add(Subject("SCPY 151","GeneralPhysics1"))
//        subject.add(Subject("EGCO 111","ComputerProgramming"))
//        subject.add(Subject("LAEN 103","EnglishLevel1"))

    }

    class subjectAdapter(items : List<String>, items2 : List<String>, ctx : Context) : RecyclerView.Adapter<subjectAdapter.ViewHolder>(){

        var list = items
        var lists = items2
        var context = ctx

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.subjectcode?.text = list.get(position)
            holder?.subjectname?.text = lists.get(position)
        }

        override fun getItemCount(): Int {
        return list.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.each_semester,parent,false))
        }


        class ViewHolder(v:View) : RecyclerView.ViewHolder(v){
            val subjectcode = v.subjectCode
            val subjectname = v.subjectName
        }

    }

    fun loadData(){
        subjectCode.add("SCMA 115")
        subjectCode.add("SCPY 110")
        subjectCode.add("SCPY 151")
        subjectCode.add("EGCO 111")
        subjectCode.add("LAEN 103")

        subjectName.add("Calculus")
        subjectName.add("PhysicsLaboratory1")
        subjectName.add("GeneralPhysics1")
        subjectName.add("ComputerProgramming")
        subjectName.add("EnglishLevel1")
    }

}