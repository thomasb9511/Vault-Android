package com.lobstr.stellar.vault.presentation.vault_auth.signer_info


import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.faq.FaqFragment
import com.lobstr.stellar.vault.presentation.home.settings.show_public_key.ShowPublicKeyDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.LobstrWallet.DEEP_LINK_MULTISIG_SETUP
import com.lobstr.stellar.vault.presentation.util.Constant.LobstrWallet.PACKAGE_NAME
import com.lobstr.stellar.vault.presentation.util.Constant.Social.STORE_URL
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer.RecheckSignerFragment
import kotlinx.android.synthetic.main.fragment_signer_info.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class SignerInfoFragment : BaseFragment(),
    SignerInfoView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = SignerInfoFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: SignerInfoPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideSignerInfoPresenter() = SignerInfoPresenter()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = if (mView == null) inflater.inflate(
            R.layout.fragment_signer_info,
            container,
            false
        ) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnDownloadLobstrApp.setOnClickListener(this)
        btnOpenLobstrApp.setOnClickListener(this)
        btnCopyUserPk.setOnClickListener(this)
        btnShowQr.setOnClickListener(this)
        btnNext.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.signer_info, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> mPresenter.infoClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            btnDownloadLobstrApp.id -> mPresenter.downloadLobstrAppClicked()
            btnOpenLobstrApp.id -> mPresenter.openLobstrAppClicked()
            btnCopyUserPk.id -> mPresenter.copyUserPublicKeyClicked()
            btnShowQr.id -> mPresenter.showQrClicked()
            btnNext.id -> mPresenter.btnNextClicked()
        }
    }

    override fun checkExistenceLobstrApp() {
        val packageManager = requireActivity().packageManager
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = packageManager.getApplicationInfo(PACKAGE_NAME, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        cvLobstrWalletInfo.visibility = if (applicationInfo != null) View.VISIBLE else View.GONE
        cvLobstrWalletInstall.visibility = if (applicationInfo != null) View.GONE else View.VISIBLE

        // Start check of Lobstr Wallet app install.
        mPresenter.startCheckExistenceLobstrAppWithInterval(applicationInfo == null)
    }

    override fun setupUserPublicKey(userPublicKey: String?) {
        tvUserPublicKey.text = userPublicKey
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    override fun showRecheckSignerScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                RecheckSignerFragment::class.qualifiedName!!
            ),
            R.id.fl_container
        )
    }

    override fun showHelpScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                FaqFragment::class.qualifiedName!!
            ),
            R.id.fl_container
        )
    }

    override fun showPublicKeyDialog(publicKey: String) {
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_PUBLIC_KEY, publicKey)

        val dialog = ShowPublicKeyDialogFragment()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.PUBLIC_KEY)
    }

    override fun downloadLobstrApp() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(STORE_URL.plus(PACKAGE_NAME))
            )
        )
    }

    override fun openLobstrMultisigSetupScreen() {
        try {
            // Try open lobstr multisignature setup screen by deep link.
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DEEP_LINK_MULTISIG_SETUP)))
        } catch (exc: ActivityNotFoundException) {
            // Otherwise try open Lobstr App.
            openLobstrApp()
        }
    }

    override fun openLobstrApp() {
        try {
            startActivity(requireActivity().packageManager.getLaunchIntentForPackage(PACKAGE_NAME))
        } catch (exc: Exception) {
            Toast.makeText(context, R.string.msg_no_app_found, Toast.LENGTH_SHORT).show()
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
