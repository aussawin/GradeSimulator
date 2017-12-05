package com.egco428.gradesimulator

import java.io.Serializable

data class Course(var courseNo: String = "",
                  val name: String,
                  val category: Int,
                  val credit: Int,
                  var categoryName: String,
                  val requisite: Requisite?,
                  var prerequisite: String?
        ) : Serializable {
        constructor(): this("","",0,0,"", null,null)
}