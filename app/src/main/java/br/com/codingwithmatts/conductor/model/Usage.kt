package br.com.codingwithmatts.conductor.model

class Usage( private var month : String, private var name : String, private var value : Double ) {

    fun getMonth() : String {
        return month
    }

    fun getName() : String {
        return name
    }

    fun getValue() : Double {
        return value
    }
}
