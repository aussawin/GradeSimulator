package com.egco428.gradesimulator

data class Course(var courseNo: String = "",
                  val name: String,
                  val category: Int,
                  val credit: Int,
                  var categoryName: String,
                  val requisite: Requisite?,
                  var prerequisite: String?
        ) {
        constructor(): this("","",0,0,"", null,null)
}