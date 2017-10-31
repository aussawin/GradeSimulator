package com.egco428.gradesimulator

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_course_list.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.course_row.view.*

class CourseListActivity : AppCompatActivity() {

    val courseArray = ArrayList<course>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setListView()

    }

    private fun setListView(){
        courseArray.add(course("General Education for Human Development", "MUGE 101","GEN"))
        courseArray.add(course("Social Studies for Human Development", "MUGE 102","GEN"))
        courseArray.add(course("Arts and Science for Human Development", "MUGE 103","GEN"))
        courseArray.add(course("Philosophy,Ethics and Laws for Engineers", "EGID 300","GEN"))
        courseArray.add(course("Art of Using Thai Language in Communication", "LATH 100","ART"))
        courseArray.add(course("English Level 1", "LAEN 103","ART"))
        courseArray.add(course("English Level 2", "LAEN 104","ART"))
        courseArray.add(course("English Level 3", "LAEN 105","ART"))
        courseArray.add(course("English Level 4", "LAEN 106","ART"))
        val cookiesArrayAdapter = CookiesArrayAdapter(this,0,courseArray!!)
        courseListView.adapter = cookiesArrayAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.courselist_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(item!!.itemId == R.id.saveBtn){
            finish()
            return true
        }
        else if(item!!.itemId == android.R.id.home) {
            finish()
            return true
        }
        else {
            return super.onOptionsItemSelected(item)
        }

    }
    private class CookiesArrayAdapter(var context: Context, resource: Int, var objects: ArrayList<course>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val course = objects[position]

            if(convertView == null){
                val layoutInflator = LayoutInflater.from(parent!!.context)
                view = layoutInflator.inflate(R.layout.course_row,parent,false)
                val viewHolder = ViewHolder(view.courseTitle,view.courseCode)
                view.tag = viewHolder
            }
            else {
                view = convertView
            }
            val viewHolder = view.tag as ViewHolder

            viewHolder.titleTextView.text = course.title
            viewHolder.codeTextView.text = course.code

            return view
        }

        override fun getCount(): Int {
            return objects.size
        }

        override fun getItem(position: Int): Any {
            return objects[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        private class ViewHolder(val titleTextView: TextView, val codeTextView: TextView){

        }

    }
}
