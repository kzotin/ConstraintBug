package com.kzotin.constraint

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_pin_auth.view.*

class PinAuthView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    interface Callback {
        fun onInputModeChange(fingerprintMode: Boolean)
        fun onForgotPin()
    }

    var callback: Callback? = null

    var useFingerprint = false
    var fingerprintMode = false
        private set(value) {
            field = value
            if (value) clearInputPin()
            callback?.onInputModeChange(value)
        }

    private var pinInputDist = 0
    private var logoInputViewDist = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_pin_auth, this, true)

        pinKeyboard.setTarget(pinInputView)
        forgotPinBt.setOnClickListener { callback?.onForgotPin() }
    }

    fun clearInputPin() {
        pinInputView?.text = null
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            pinInputDist = pinInputView.bottom - logo.top
            logoInputViewDist = (pinInputDist * LOGO_DIST_PARALLAX).toInt()

            setupTransition(if (fingerprintMode) 1f else 0f)
        }
    }

    private fun setupTransition(fraction: Float) {
        val pinDist = pinInputDist * fraction
        pinKeyboard.translationY = pinDist
        forgotPinLay.translationY = pinDist
        notClientLay.translationY = pinDist

        val pinAlpha = (0.5f - fraction) * 5
        titlePin.alpha = pinAlpha
        pinInputView.alpha = pinAlpha
        pinKeyboard.alpha = pinAlpha
        forgotPinBt.alpha = pinAlpha
        notClientBt.alpha = pinAlpha

        val fpDist = logoInputViewDist * fraction
        logo.translationY = fpDist
        titleFingerprint.translationY = fpDist
        titlePin.translationY = fpDist
        pinInputView.translationY = fpDist

        val fpAlpha = (fraction - 0.5f) * 5
        titleFingerprint.alpha = fpAlpha

        bankLogo.alpha = (fraction - 0.6f) * 5
    }

    companion object {
        private const val LOGO_DIST_PARALLAX = 0.4f
    }
}