package com.retail.dolphinpos.presentation.features.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.retail.dolphinpos.presentation.util.Utils

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB
    abstract fun inflateBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateBinding()
        setContentView(binding.root)
        Utils.hideStatusBar(this)
        window.setImmersiveFullScreen()
    }

    override fun onResume() {
        super.onResume()
        Utils.hideStatusBar(this)
        window.setImmersiveFullScreen()
    }

}