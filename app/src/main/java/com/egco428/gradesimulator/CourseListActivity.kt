package com.egco428.gradesimulator

import android.app.Activity
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
import android.content.Intent
import java.io.Serializable

class CourseListActivity : AppCompatActivity() {
    private val courseArray: ArrayList<Course> = arrayListOf()
    private val courseShowArray: ArrayList<Course> = arrayListOf()
    private val categoryArray = HashMap<Int,String>()
    private val checkList: ArrayList<CheckPosition> = arrayListOf()
    private lateinit var dataReference: DatabaseReference
    private lateinit var categoryReference: DatabaseReference
    private val categorySet: ArrayList<String> = arrayListOf()
    private lateinit var itemsCheckList: BooleanArray
    private lateinit var obj: ArrayList<Course>
    private var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val itemCheck= arrayListOf<Boolean>()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Receive data from CourseRegistedActivity
        if (intent.extras.getLong("position") != null) {
            if (intent.getBundleExtra("arrayList") != null) {
                val temp = intent.getBundleExtra("arrayList")
                obj = temp.getSerializable("arrayList_2") as ArrayList<Course>
            }

            position = intent.extras.getInt("position")
        }

        //Get data from Firebase
        categoryReference = FirebaseDatabase.getInstance().getReference("courseCategory")
        dataReference = FirebaseDatabase.getInstance().getReference("courseData")

        categoryReference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) { }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                for(i in dataSnapshot!!.children){
                    categoryArray.put(Integer.parseInt(i.key),i.value.toString())
                    categorySet.add(i.value.toString())
                    itemCheck.add(true)
                }

                itemsCheckList = itemCheck.toBooleanArray()
                setCourseShow()
            }
        })

        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                for (i in dataSnapshot!!.children) {
                    val message = i.getValue(Course::class.java) as Course
                    var checkBoolean = false

                    message.courseNo = i.key
                    message.categoryName = categoryArray.getValue(message.category)
                    courseArray.add(message)

                    if (message.requisite?.year == position!! / 3 + 1 && message.requisite.semester == position!! % 3 + 1) {
                        checkBoolean = true
                    }

                    obj
                            .filter { it.courseNo == message.courseNo }
                            .forEach { checkBoolean = true }

                    checkList.add(CheckPosition(courseArray.indexOf(message), checkBoolean))
                }

                setCourseShow()
            }
        })

        //Filter
        filterBtn.setOnClickListener {
            val dialog = FilterDialog()
            val ft = fragmentManager.beginTransaction()
            val prev = fragmentManager.findFragmentByTag("dialog")
            val args = Bundle()

            args.putStringArray("category", categorySet.toTypedArray())
            args.putBooleanArray("itemsChecked",itemsCheckList)
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialog.arguments = args
            dialog.setContext(this)
            dialog.show(ft, "dialog")
        }

        //Search
        searchTextBox.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                setCourseShow()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })

        courseListView.adapter = CourseArrayAdapter(this, 0, courseArray, checkList)
    }

    private fun setCourseShow(){
        val searchText = searchTextBox.text.toString().toLowerCase()
        val tempCheckArray = arrayListOf<CheckPosition>()
        courseShowArray.clear()

        //Filter Method
        (0 until this.itemsCheckList.size)
                .filter { this.itemsCheckList[it] }
                .forEach { checkStateItem ->
                    courseArray.filterTo(courseShowArray) { it.category == checkStateItem + 1 }
                }

        val tempCourseShow = arrayListOf<Course>()
        tempCourseShow.addAll(courseShowArray)
        courseShowArray.clear()
        Log.d("search",searchText)
        //Search Method
        for (i in tempCourseShow){
            if (i.courseNo.toLowerCase().contains(searchText.toLowerCase()) || i.name.toLowerCase().contains(searchText.toLowerCase())){

                val index = courseArray.indexOf(i)
                tempCheckArray.add(checkList[index])
                courseShowArray.add(i)
            }
        }

        setListView(courseShowArray, tempCheckArray)
    }
    fun setCategoryFilter(itemsCheckList: BooleanArray){

        this.itemsCheckList = itemsCheckList
        setCourseShow()
    }

    private fun setListView(list: ArrayList<Course>, boolList: ArrayList<CheckPosition>){
        courseListView.adapter = CourseArrayAdapter(this, 0, list, boolList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.courselist_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when {
            item!!.itemId == R.id.saveBtn -> {
                val selectedCourse: ArrayList<Course> = arrayListOf()

                checkList
                        .filter { it.isCheck }
                        .mapTo(selectedCourse) { courseArray[it.position] }

                val intent = Intent(this, CourseRegistedActivity::class.java)

                val args = Bundle()

                args.putSerializable("tempKey", selectedCourse as Serializable)

                intent.putExtra("courseObject", args)
                intent.putExtra("returnPosition", position)

                setResult(Activity.RESULT_OK, intent)

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

    class FilterDialog: DialogFragment() {
        private lateinit var categorySet: Array<String>
        private lateinit var itemsChecked: BooleanArray
        private lateinit var courseListActivity: CourseListActivity
        fun setContext(tempListActivity: CourseListActivity){
            courseListActivity = tempListActivity
        }
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            Log.d("Check","Created")
            val builder = AlertDialog.Builder(activity)
            categorySet = arguments.getStringArray("category")
            itemsChecked = arguments.getBooleanArray("itemsChecked")
            builder.setTitle("Choose filter")
                    .setMultiChoiceItems(categorySet, itemsChecked) { dialog, which, isChecked ->
                        itemsChecked[which] = isChecked
                        Log.d("Check",isChecked.toString())
                    }
                    .setPositiveButton("OK", { dialog, id ->
                        courseListActivity.setCategoryFilter(itemsChecked)
                    })
            return builder.create()
        }
    }

    private class CourseArrayAdapter(var context: Context, resource: Int, var objects: ArrayList<Course>, var checkList: ArrayList<CheckPosition>): BaseAdapter() {
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
                checkList[position].isCheck = b
                Log.d("checked", "position : $position, is $b, size : ${checkList.size}")
            })

            viewHolder.titleTextView.text = course.name
            viewHolder.codeTextView.text = course.courseNo
            viewHolder.creditText.text = "หน่วยกิต : " + course.credit.toString()
            viewHolder.categoryText.text = course.categoryName
            viewHolder.checkBox.isChecked = checkList[position].isCheck

            view.setOnClickListener {
                viewHolder.checkBox.isChecked = !viewHolder.checkBox.isChecked
                checkList[position].isCheck = viewHolder.checkBox.isChecked
            }

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

    private class CheckPosition(val position: Int,
                                var isCheck: Boolean)
}