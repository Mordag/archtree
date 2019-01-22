package archtree.activity

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.util.Log
import archtree.ArchTreeResource
import archtree.viewmodel.BaseViewModel
import autotarget.util.HasFragmentFlow

open class ActivityResource<ViewModel : BaseViewModel>
constructor(builder: ActivityBuilder<ViewModel>) : ArchTreeResource<ViewModel>(builder) {

    var viewModel: ViewModel? = null
        private set

    val fragmentFlow: HasFragmentFlow? = builder.fragmentFlow
    val systemUiVisibility: Int = builder.systemUiVisibility

    val hideSupportBar: Boolean = builder.hideSupportBar

    @SuppressLint("LogNotTimber")
    open fun onCreateViewModel(activity: FragmentActivity, factory: ViewModelProvider.Factory,
                               savedInstanceBundle: Bundle?) {

        viewModel = ViewModelProviders.of(activity, factory).get(viewModelClass!!)

        if (binding != null && bindingKey != -1) binding?.setVariable(bindingKey, viewModel)
        else Log.w(ActivityResource::class.java.name, "ViewModel is not attached to layout.")

        binding?.setLifecycleOwner(activity)
        if (lifecycleOwnerBindingKey != -1) binding?.setVariable(lifecycleOwnerBindingKey, activity as LifecycleOwner)

        if (!skipViewModelInit) viewModel?.init(false, resourceBundle, savedInstanceBundle)
    }

    open fun getLayer(): ActivityLayer<ViewModel> {
        return super.layer as ActivityLayer<ViewModel>
    }
}