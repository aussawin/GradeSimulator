package com.egco428.gradesimulator

/**
 * Created by Aussawin on 11/2/2017 AD.
 */

data class Course(val courseNo: String = "",
                  val name: String = "",
                  val cat: String = "",
                  val category: Int = 0,
                  val credit: Int = 0,
                  val requisite: MutableList<Int> = mutableListOf(),
                  val prerequisite: MutableList<String> = mutableListOf())