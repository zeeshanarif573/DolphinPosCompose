package com.retail.dolphinpos.presentation.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.retail.dolphinpos.presentation.R
import com.retail.dolphinpos.presentation.features.ui.auth.AuthActivity

object Utils {

    private var progressDialog: Dialog? = null

    // ✅ Show Loader
//    fun showLoader(context: Context, message: String = "Loading...") {
//        if (progressDialog?.isShowing == true) return
//
//        progressDialog = Dialog(context).apply {
//            requestWindowFeature(Window.FEATURE_NO_TITLE)
//            setContentView(R.layout.dialog_progress)
//            setCancelable(false)
//            window?.setBackgroundDrawableResource(android.R.color.transparent)
//
//            // Start animation on ImageView
//            val imageView = findViewById<ImageView>(R.id.ivLoader)
//            val anim = AnimationUtils.loadAnimation(context, R.anim.rotate)
//            imageView.startAnimation(anim)
//
//            // Set message text
//            findViewById<TextView>(R.id.tvMessage)?.text = message
//            show()
//        }
//    }

    // ✅ Hide Loader
    fun hideLoader() {
        progressDialog?.dismiss()
        progressDialog = null
    }


    // ✅ Error Dialog
//    fun showCustomErrorDialog(
//        context: Context,
//        message: String,
//        buttonText: String = "OK",
//        iconRes: Int? = R.drawable.cross_red,
//        cancellable: Boolean = false,
//        onActionClick: (() -> Unit)? = null
//    ) {
//        val binding = DialogErrorBinding.inflate(LayoutInflater.from(context))
//
//        val dialog = AlertDialog.Builder(context)
//            .setView(binding.root)
//            .setCancelable(cancellable)
//            .create()
//
//        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
//        binding.tvMessage.text = message
//        binding.btnTryAgain.text = buttonText
//        iconRes?.let { binding.imgIcon.setImageResource(it) }
//        binding.btnTryAgain.setOnClickListener {
//            dialog.dismiss()
//            onActionClick?.invoke()
//        }
//
//        binding.btnClose.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }

    fun showLogoutDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout(context)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout(context: Context) {
        // Launch AuthActivity and navigate to PinFragment
        val intent = Intent(context, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "pin")
        }

        context.startActivity(intent)

        if (context is Activity) {
            context.finish()
        }
    }

    // ✅ Handle Button Disability
    fun Button.setEnabledWithAlpha(isEnabled: Boolean, alphaWhenDisabled: Float = 0.4f) {
        this.isEnabled = isEnabled
        this.alpha = if (isEnabled) 1f else alphaWhenDisabled
    }

    fun String.capitalizeFirst(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

    fun hideStatusBar(appCompatActivity: AppCompatActivity) {
        val window = appCompatActivity.window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}