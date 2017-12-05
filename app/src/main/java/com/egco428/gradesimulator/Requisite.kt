package com.egco428.gradesimulator

import java.io.Serializable

/**
 * Created by MailBot on 12/2/2017.
 */
class Requisite(val semester: Int,val year: Int) : Serializable {
    constructor(): this(0,0)
}