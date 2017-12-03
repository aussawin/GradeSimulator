package com.egco428.gradesimulator

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_course_list.*
import kotlinx.android.synthetic.main.course_row.view.*
import com.google.firebase.database.DataSnapshot
import android.app.AlertDialog

class CourseListActivity : AppCompatActivity() {
    private val courseArray: ArrayList<Course> = arrayListOf()
    private val categoryArray = HashMap<Int,String>()
    private var checkList: ArrayList<Boolean> = arrayListOf()
    private lateinit var dataReference: DatabaseReference
    private lateinit var categoryReference: DatabaseReference
    private val categorySet: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        categoryReference = FirebaseDatabase.getInstance().getReference("courseCategory")
        dataReference = FirebaseDatabase.getInstance().getReference("courseData")

        categoryReference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) { }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                for(i in dataSnapshot!!.children){
                    categoryArray.put(Integer.parseInt(i.key),i.value.toString())
                    categorySet.add(i.value.toString())
                }
            }
        })

        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                for (i in dataSnapshot!!.children) {
                    val message = i.getValue(Course::class.java) as Course

                    message.courseNo = i.key
                    message.categoryName = categoryArray.getValue(message.category)
                    courseArray.add(message)
                    checkList.add(false)
                }
            }
        })

        filterBtn.setOnClickListener {
            Log.d("Category",categorySet.toString())
            val dialog = FilterDialog()
            val ft = fragmentManager.beginTransaction()
            val prev = fragmentManager.findFragmentByTag("dialog")
            val args = Bundle()

            args.putStringArray("category", categorySet.toTypedArray())
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialog.arguments = args
            // Create and show the dialog.
            dialog.show(ft, "dialog")
        }

        searchTextBox.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                val searchText = searchTextBox.text.toString().toLowerCase()
                val tempArray = ArrayList<Course>()

                courseArray.filterTo(tempArray) { it.courseNo.toLowerCase().contains(searchText) || it.name.toLowerCase().contains(searchText) }

                setListView(tempArray)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })

        courseListView.adapter = CourseArrayAdapter(this, 0, courseArray, checkList)

    }

    private fun setListView(list: ArrayList<Course>){
        courseListView.adapter = CourseArrayAdapter(this, 0, list, checkList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.courselist_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when {
            item!!.itemId == R.id.saveBtn -> {
                finish()
                true
            }
            item.itemId == android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    class FilterDialog: DialogFragment(){
        private lateinit var categorySet: Array<String>

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(activity)
            categorySet = arguments.getStringArray("category")
            builder.setTitle("Fire ?")
                    .setMultiChoiceItems(categorySet,null) { dialog, which, isChecked -> }
                    .setPositiveButton("OK", { dialog, id ->
                        // FIRE ZE MISSILES!
                    })
                    .setNegativeButton("Cancel", { dialog, id ->
                        // User cancelled the dialog
                    })

            // Create the AlertDialog object and return it
            return builder.create()
        }

    }

    private class CourseArrayAdapter(var context: Context, resource: Int, var objects: ArrayList<Course>, var checkList: ArrayList<Boolean>): BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val course = objects[position]

            if (convertView == null) {
                val layoutInflator = LayoutInflater.from(parent!!.context)
                view = layoutInflator.inflate(R.layout.course_row,parent,false)
                val viewHolder = ViewHolder(view.courseTitle, view.courseCode, view.crediteTextView, view.categoryTextView, view.checkBox)
                view.tag = viewHolder
            }
            else {
                view = convertView
            }

            val viewHolder = view.tag as ViewHolder

            viewHolder.checkBox.setOnCheckedChangeListener({ _, b ->
                checkList[position] = b
                Log.d("checked", "position : $position, is $b, size : ${checkList.size}")
            })

            viewHolder.titleTextView.text = course.name
            viewHolder.codeTextView.text = course.courseNo
            viewHolder.creditText.text = "หน่วยกิต : " + course.credit.toString()
            viewHolder.categoryText.text = course.categoryName
            viewHolder.checkBox.isChecked = checkList[position]

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

        private class ViewHolder(val titleTextView: TextView,
                                 val codeTextView: TextView,
                                 val creditText: TextView,
                                 val categoryText: TextView,
                                 val checkBox: CheckBox)
    }
}