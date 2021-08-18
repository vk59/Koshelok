package com.tinkoff_sirius.koshelok.model

sealed class MainAdapterItem {

    object Header : MainAdapterItem()

    data class Transaction(val title: String) : MainAdapterItem()
}