package com.retail.dolphinpos.presentation.features.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

abstract class BaseFragment<Binding : ViewBinding, VM : ViewModel>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding,
    private val viewModelClass: KClass<VM>
) : Fragment() {

    protected open val vmStoreOwner: ViewModelStoreOwner
        get() = this

    private var _binding: Binding? = null
    protected val binding
        get() = _binding
            ?: throw IllegalStateException("ViewBinding is only available between onCreateView and onDestroyView")

    protected val viewModel: VM by lazy {
        ViewModelProvider(vmStoreOwner, defaultViewModelProviderFactory)[viewModelClass.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        Log.d("GenericBaseFragment", "onCreateView: Binding initialized.")
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("GenericBaseFragment", "onDestroyView: Binding cleared.")
    }

    /**
     * Navigate to another fragment safely.
     */
    protected fun goToNextFragment(destination: Int, bundle: Bundle? = null) {
        try {
            if (isAdded) {
                findNavController().navigate(destination, bundle)
                Log.d("GenericBaseFragment", "Navigating to destination: $destination")
            } else {
                Log.w("GenericBaseFragment", "Fragment is not added, cannot navigate.")
            }
        } catch (e: Exception) {
            Log.e("GenericBaseFragment", "Navigation failed: ${e.message}", e)
        }
    }

}
