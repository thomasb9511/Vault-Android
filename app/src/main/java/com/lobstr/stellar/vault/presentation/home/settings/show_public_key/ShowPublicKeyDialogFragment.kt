package com.lobstr.stellar.vault.presentation.home.settings.show_public_key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseBottomSheetDialog
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import kotlinx.android.synthetic.main.fragment_show_public_key.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import net.glxn.qrgen.android.QRCode


class ShowPublicKeyDialogFragment : BaseBottomSheetDialog(), ShowPublicKeyView,
    View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ShowPublicKeyDialogFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: ShowPublicKeyPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideShowPublicKeyPresenter() = ShowPublicKeyPresenter(
        arguments?.getString(Constant.Bundle.BUNDLE_PUBLIC_KEY)!!
    )

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
            R.layout.fragment_show_public_key,
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
        btnCopyKey.setOnClickListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            btnCopyKey.id -> {
                AppUtil.closeKeyboard(activity)
                mPresenter.copyPublicKeyClicked()
            }
        }
    }

    override fun setupPublicKey(publicKey: String) {
        val qrCodeImage = QRCode.from(publicKey).withColor(
            ContextCompat.getColor(requireContext(), R.color.color_primary),
            ContextCompat.getColor(requireContext(), android.R.color.transparent)
        ).bitmap()

        ivUserPublicKeyQrCode.setImageBitmap(qrCodeImage)

        tvPublicKey.text = AppUtil.ellipsizeStrInMiddle(publicKey, PK_TRUNCATE_COUNT)
    }

    override fun copyToClipBoard(text: String) {
        dismiss()
        AppUtil.copyToClipboard(context, text)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}