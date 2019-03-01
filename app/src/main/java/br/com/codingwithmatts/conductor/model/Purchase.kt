package br.com.codingwithmatts.conductor.model


class Purchase(
    private var date: String,
    private var store: String,
    private var description: String,
    private var value: Double) {

    fun getDate() : String {
        return date
    }

    fun getStore() : String {
        return store
    }

    fun getDescription() : String {
        return description
    }

    fun getValue() : Double {
        return value
    }
}
