package com.lobstr.stellar.vault.presentation.dagger.module.account_name

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.account_name.AccountNameInteractor
import com.lobstr.stellar.vault.domain.account_name.AccountNameInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object AccountNameModule {
    @Provides
    fun provideAccountNameInteractor(
        accountRepository: AccountRepository
    ): AccountNameInteractor {
        return AccountNameInteractorImpl(accountRepository)
    }
}