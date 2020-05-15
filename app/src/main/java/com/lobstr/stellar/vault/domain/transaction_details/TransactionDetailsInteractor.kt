package com.lobstr.stellar.vault.domain.transaction_details

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.Single
import org.stellar.sdk.AbstractTransaction
import org.stellar.sdk.responses.SubmitTransactionResponse


interface TransactionDetailsInteractor {

    fun retrieveActualTransaction(transactionItem: TransactionItem): Single<TransactionItem>

    fun confirmTransactionOnHorizon(
        transaction: AbstractTransaction
    ): Single<SubmitTransactionResponse>

    fun confirmTransactionOnServer(
        needAdditionalSignatures: Boolean,
        transactionStatus: Int?,
        hash: String?,
        transaction: String
    ): Single<String>

    fun cancelTransaction(hash: String): Single<TransactionItem>

    fun getPhrases(): Single<String>

    fun isTrConfirmationEnabled(): Boolean

    fun getTransactionSigners(
        xdr: String,
        sourceAccount: String
    ): Single<AccountResult>

    fun getStellarAccount(stellarAddress: String): Single<Account>

    fun signTransaction(transaction: String): Single<AbstractTransaction>
}