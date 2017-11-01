package com.egco428.gradesimulator

import java.util.ArrayList

/**
 * Created by Aussawin on 11/1/2017 AD.
 */
class ExpandableListDataPump {
    fun getData(): HashMap<String, List<Course>>{
        val expandableListDetail: HashMap<String, List<Course>> = HashMap()
        val y1s1: List<Course> = (1..5).map { Course("S1_NO$it", "S1_SUBJ$it") }
        val y1s2: List<Course> = (1..5).map { Course("S2_NO$it", "S2_SUBJ$it") }
        expandableListDetail.put("Year1Semester1", y1s1)
        expandableListDetail.put("Year1Semester2", y1s2)
        return expandableListDetail;
    }
}