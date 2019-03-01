package br.com.codingwithmatts.conductor.model


class History(private var purchases: List<Purchase>, private var lastPage : Int) {

    fun getHistory() : List<Purchase> {
        return purchases
    }

    fun getLastPage() : Int {
        return lastPage
    }

}
