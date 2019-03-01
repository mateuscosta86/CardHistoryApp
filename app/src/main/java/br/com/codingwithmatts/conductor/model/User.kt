package br.com.codingwithmatts.conductor.model

class User(private var name: String, private var cardNumber: String, private var expirationDate: String) {

    fun getName() : String {
        return name
    }

    fun getCardNumber() : String {
        return cardNumber
    }

    fun getExpirationDate() : String {
        return expirationDate
    }
}
