package com.tinkoffsirius.koshelok

import android.content.Context
import com.tinkoffsirius.koshelok.repository.AccountSharedRepository
import com.tinkoffsirius.koshelok.repository.PosedTransactionSharedRepository
import com.tinkoffsirius.koshelok.repository.SharedPreferencesFactory
import com.tinkoffsirius.koshelok.repository.WalletRepository
import com.tinkoffsirius.koshelok.ui.ResourceProvider
import com.tinkoffsirius.koshelok.ui.main.MainViewModelFactory
import com.tinkoffsirius.koshelok.ui.transactionediting.TransactionEditingViewModelFactory

object Dependencies {

    var context: Context? = null

    val walletRepository by lazy { WalletRepository() }

    val accountRepository by lazy {
        AccountSharedRepository(
            SharedPreferencesFactory().create(
                context!!,
                SharedPreferencesFactory.ACCOUNT_DATA
            )
        )
    }

    val mainViewModelFactory by lazy {
        MainViewModelFactory(
            accountRepository = accountRepository,
            repository = walletRepository
        )
    }

    val transactionRepository by lazy {
        PosedTransactionSharedRepository(
            SharedPreferencesFactory().create(
                context!!,
                SharedPreferencesFactory.TRANSACTION_DATA
            )
        )
    }

    val transactionViewModelFactory by lazy {
        TransactionEditingViewModelFactory(
            transactionRepository = transactionRepository,
            accountRepository = accountRepository,
            repository = walletRepository
        )
    }

    val resourceProvider by lazy {
        ResourceProvider(context!!)
    }
}
