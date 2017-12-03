package com.egco428.gradesimulator

import java.time.Year
import java.util.ArrayList

/**
 * Created by Aussawin on 11/1/2017 AD.
 */
class ExpandableListDataPump {
    fun getData(): HashMap<String, List<Course>>{
        val expandableListDetail: HashMap<String, List<Course>> = HashMap()
        val YearsList = ArrayList<EducationYear>()

        for(i in 1..4){
            val yearData = EducationYear()
            for(j in 1..3){
                val semesterData = Semester()
                yearData.semesterlist.add(semesterData)
            }
            YearsList.add(yearData)
        }
//        val y1s1: List<Course> = (1..5).map { Course("S1_NO$it", "S1_SUBJ$it", "CAT${it%3}") }
//        val y1s2: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it", "CAT${it%3}") }
//        val y1s3: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it", "CAT${it%3}") }
//        val y2s1: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it", "CAT${it%3}") }
//        val y2s2: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it", "CAT${it%3}") }
//        val y2s3: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it", "CAT${it%3}") }
//        val y3s1: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it", "CAT${it%3}") }
//        val y3s2: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it", "CAT${it%3}") }
//        val y3s3: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it", "CAT${it%3}") }
//        val y4s1: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it", "CAT${it%3}") }
//        val y4s2: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it", "CAT${it%3}") }

        var SubjectNameTest: String

        for (i in 1..4){
            for (j in 1..3){
                for(z in 1..3){
                    SubjectNameTest = "Engineering in Year "+i+" in Semester "+j+" subject "+z
                    YearsList.get(i-1).semesterlist.get(j-1).courseList.add(Course(SubjectNameTest,"testnaja","test"))
                }

            }
        }

        var NameForYear: String

        for (i in 0..3){
            for (j in 0..2){
                NameForYear = "Year"+(i+1)+"Semester"+(j+1)
                expandableListDetail.put(NameForYear, YearsList.get(i).semesterlist.get(j).courseList.toList())
            }
        }

        return expandableListDetail
    }
}