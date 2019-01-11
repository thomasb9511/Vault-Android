package com.lobstr.stellar.vault.domain.signed_account

import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.Single

interface SignedAccountInteractor {

    fun getSignedAccounts(): Single<List<Account>>
}