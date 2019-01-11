package com.lobstr.stellar.vault.domain.signed_account

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single

class SignedAccountInteractorImpl(
    private val accountRepository: AccountRepository,
    private val presfUtil: PrefsUtil
) : SignedAccountInteractor {

    override fun getSignedAccounts(): Single<List<Account>> {
        return accountRepository.getSignedAccounts(AppUtil.getJwtToken(presfUtil.authToken))
            .doOnSuccess { presfUtil.accountSignersCount = it.size }
    }
}