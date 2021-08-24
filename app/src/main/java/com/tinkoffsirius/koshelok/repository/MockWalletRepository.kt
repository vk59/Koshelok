package com.tinkoffsirius.koshelok.repository

import com.tinkoffsirius.koshelok.repository.entities.*
import com.tinkoffsirius.koshelok.service.SampleNetworkService
import io.reactivex.rxjava3.core.Single

class MockWalletRepository : WalletRepository {

    override fun getWalletById(id: Long, idUser: String, idToken: String): Single<WalletData> {
        return SampleNetworkService.getWalletById(id, idUser, idToken)
    }

    override fun deleteTransactionById(id: Long): Single<Response> {
        return SampleNetworkService.deleteTransactionById(id)
    }

    override fun createTransaction(
        transactionData: CreateTransactionData,
        idUser: String,
        idToken: String
    ): Single<Response> {
        return SampleNetworkService.createTransaction(transactionData, idUser, idToken)
    }

    override fun getCategories(
        transactionType: String,
        idUser: String,
        idToken: String
    ): Single<List<CategoryData>> {
        return SampleNetworkService.getCategories(transactionType, idUser, idToken)
    }

    override fun updateTransaction(
        transactionData: CreateTransactionData,
        idUser: String,
        idToken: String
    ): Single<Response> {
        return SampleNetworkService.updateTransaction(transactionData, idUser, idToken)
    }

    override fun createWallet(walletData: CreateWalletData, idUser: String, idToken: String): Single<Response> {
        return Single.just(Response("Successfully saved wallet $walletData"))
    }

    override fun updateWallet(walletData: CreateWalletData, idUser: String, idToken: String): Single<Response> {
        return Single.just(Response("Successfully updated wallet $walletData"))
    }

    override fun getUserInfoWallets(idUser: Long, idToken: String): Single<UserInfoWallets> {
        return SampleNetworkService.getUserInfoWallets(idUser, idToken)
    }

    override fun deleteWalletById(id: Long, idUser: String, idToken: String): Single<Response> {
        return SampleNetworkService.deleteWalletById(id, idUser, idToken)
    }
}
