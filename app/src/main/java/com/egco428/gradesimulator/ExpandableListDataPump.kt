package com.egco428.gradesimulator

import java.util.ArrayList

/**
 * Created by Aussawin on 11/1/2017 AD.
 */
class ExpandableListDataPump {
    fun getData(): HashMap<String, List<Course>>{
        val expandableListDetail: HashMap<String, List<Course>> = hashMapOf()
        val yearsList = arrayListOf<EducationYear>()

        val years = arrayListOf(1, 2, 3, 4)
        val semesters = arrayListOf(1, 2, 3)

        for(i in years) {
            val yearData = EducationYear()

            for(j in semesters) {
                val semesterData = Semester()
                yearData.semesterList.add(semesterData)
            }

            yearsList.add(yearData)
        }

        var subjectNameTest: String

        for (i in years){
            for (j in semesters){
                for(z in 1..3){
                    subjectNameTest = "EG$i$j$z"
                    yearsList[i-1].semesterList[j-1].courseList.add(Course(subjectNameTest,
                            "test",
                            0,
                            0,
                            "test",
                            Requisite(0, 0),
                            "test"))
                }
            }
        }

        var nameForYear: String
        for (i in 0..3){
            for (j in 0..2){
                nameForYear = "Year"+(i+1)+"Semester"+(j+1)
                expandableListDetail.put(nameForYear, yearsList[i].semesterList[j].courseList.toList())
            }
        }

        return expandableListDetail
    }
}