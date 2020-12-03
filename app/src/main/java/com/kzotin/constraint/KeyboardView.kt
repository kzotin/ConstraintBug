package com.kzotin.constraint

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.item_keyboard.view.*

class KeyboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnTouchListener, View.OnClickListener {

    private var targetTextView: TextView? = null
    private val keyListener: KeyListener
    private val pressHandler = Handler(Looper.getMainLooper())

    private val numberViews by lazy {
        listOf(n0, n1, n2, n3, n4, n5, n6, n7, n8, n9)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_keyboard, this, true)

        keyListener = createKeyTextListener()
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val isAccessibilityEnabled = am.isEnabled
        val isExploreByTouchEnabled = am.isTouchExplorationEnabled
        if (isAccessibilityEnabled && isExploreByTouchEnabled) {
            numberViews.forEach { it.setOnClickListener(this) }
            backspace.setOnClickListener(this)
        } else {
            numberViews.forEach { it.setOnTouchListener(this) }
            backspace.setOnTouchListener(this)
        }
    }

    private val text: String?
        get() = targetTextView?.text.toString()

    private fun createKeyTextListener(): KeyListener {
        return object : KeyListener {

            override fun onKeyPressed(key: String) {
                updateOutputView(text + key)
            }

            override fun onBackSpacePressed() {
                val text = text
                if (text != null && text.isNotEmpty()) {
                    updateOutputView(text.substring(0, text.length - 1))
                }
            }
        }
    }

    private fun updateOutputView(text: String) {
        targetTextView?.text = text
    }

    private fun pressKey(view: View, key: String) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        pressHandler.post { keyListener.onKeyPressed(key) }
    }

    private fun pressBackspace(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        pressHandler.post { keyListener.onBackSpacePressed() }
    }

    fun setTarget(target: TextView?) {
        targetTextView = target
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        if (motionEvent.action != MotionEvent.ACTION_DOWN) {
            return view.onTouchEvent(motionEvent)
        }
        buttonClick(view)
        return view.onTouchEvent(motionEvent)
    }

    override fun onClick(view: View) {
        buttonClick(view)
    }

    private fun buttonClick(view: View) {
        when (view.id) {
            R.id.n0 -> pressKey(view, "0")
            R.id.n1 -> pressKey(view, "1")
            R.id.n2 -> pressKey(view, "2")
            R.id.n3 -> pressKey(view, "3")
            R.id.n4 -> pressKey(view, "4")
            R.id.n5 -> pressKey(view, "5")
            R.id.n6 -> pressKey(view, "6")
            R.id.n7 -> pressKey(view, "7")
            R.id.n8 -> pressKey(view, "8")
            R.id.n9 -> pressKey(view, "9")
            R.id.backspace -> pressBackspace(view)
        }
    }

    interface KeyListener {
        fun onKeyPressed(key: String)
        fun onBackSpacePressed()
    }
}