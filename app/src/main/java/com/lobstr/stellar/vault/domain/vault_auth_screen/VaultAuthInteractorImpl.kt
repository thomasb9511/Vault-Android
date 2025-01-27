package com.lobstr.stellar.vault.domain.vault_auth_screen

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single

class VaultAuthInteractorImpl(
    private val vaultAuthRepository: VaultAuthRepository,
    private val stellarRepository: StellarRepository,
    private val accountRepository: AccountRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val localDataRepository: LocalDataRepository,
    private val prefsUtil: PrefsUtil,
    private val fcmHelper: FcmHelper
) : VaultAuthInteractor {

    override fun hasMnemonics(): Boolean {
        return !prefsUtil.encryptedPhrases.isNullOrEmpty()
    }

    override fun hasTangem(): Boolean {
        return !prefsUtil.tangemCardId.isNullOrEmpty()
    }

    override fun getTangemCardId(): String? {
        return prefsUtil.tangemCardId
    }

    override fun getUserToken(): String? {
        return prefsUtil.authToken
    }

    override fun authorizeVault(): Single<String> {
        return getChallenge()
            .flatMap { transaction ->
                getPhrases().flatMap {
                    stellarRepository.createKeyPair(
                        it.toCharArray(),
                        prefsUtil.getCurrentPublicKeyIndex()
                    )
                }
                    .flatMap { stellarRepository.signTransaction(it, transaction) }
            }
            .flatMap { submitChallenge(it.toEnvelopeXdrBase64()) }
            .doOnSuccess {
                prefsUtil.authToken = it
                localDataRepository.saveAuthToken(prefsUtil.publicKey!!, it)
                registerFcm()
            }
    }

    override fun registerFcm() {
        fcmHelper.checkFcmRegistration()
    }

    override fun authorizeVault(transaction: String): Single<String> {
        return vaultAuthRepository.submitChallenge(transaction)
            .doOnSuccess {
                prefsUtil.authToken = it
                localDataRepository.saveAuthToken(prefsUtil.publicKey!!, it)
                registerFcm()
            }
    }

    override fun getChallenge(): Single<String> {
        return vaultAuthRepository.getChallenge(getUserPublicKey())
    }

    override fun submitChallenge(transaction: String): Single<String> {
        return vaultAuthRepository.submitChallenge(transaction)
    }

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }

    override fun getPhrases(): Single<String> {
        return Single.fromCallable {
            return@fromCallable keyStoreRepository.decryptData(
                PrefsUtil.PREF_ENCRYPTED_PHRASES,
                PrefsUtil.PREF_PHRASES_IV
            )
        }
    }

    override fun confirmAccountHasSigners() {
        prefsUtil.accountHasSigners = true
    }

    override fun getSignedAccounts(token: String): Single<List<Account>> {
        return accountRepository.getSignedAccounts(AppUtil.getJwtToken(token))
            .doOnSuccess { prefsUtil.accountSignersCount = it.size }
    }

    override fun clearUserData() {
        prefsUtil.clearUserPrefs()
        keyStoreRepository.clearAll()
    }
}