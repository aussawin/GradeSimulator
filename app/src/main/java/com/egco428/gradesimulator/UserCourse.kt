package com.egco428.gradesimulator

import java.io.Serializable

/**
 * Created by MailBot on 12/6/2017.
 */
class UserCourse(val course: Course,var grade: String,var gradeValue: Double,val yearRegisted: Int,val semesterRegisted: Int): Serializable