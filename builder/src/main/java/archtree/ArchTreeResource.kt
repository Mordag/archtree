package archtree

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import archtree.viewmodel.BaseViewModel

abstract class ArchTreeResource<ViewModel : BaseViewModel> constructor(builder: ArchTreeBuilder<ViewModel, *>) {

    protected var componentLayer: ComponentLayer<ViewModel>? = builder.componentLayer

    var view: View? = null
        private set
    var binding: ViewDataBinding? = null
        private set

    val customBundle: Bundle? = builder.customBundle

    val layoutId: Int = builder.layoutId
    val bindingKey: Int = builder.bindingKey

    val menuId: Int? = builder.menuId

    val viewModelClass: Class<ViewModel>? = builder.viewModelClass
    val viewModelInitMode: ViewModelInitMode = builder.viewModelInitMode

    val dataBindingComponent: DataBindingComponent? = builder.dataBindingComponent
    val dataBindingComponentBindingKey: Int = builder.dataBindingComponentBindingKey

    val lifecycleOwnerBindingKey: Int = builder.lifecycleOwnerBindingKey

    val toolbarViewId: Int? = builder.toolbarViewId
    val toolbarTitle: String? = builder.toolbarTitle
    val toolbarIcon: Int? = builder.toolbarIcon
    val displayHomeAsUpEnabled: Boolean = builder.displayHomeAsUpEnabled
    val parentHasToolbarView: Boolean = builder.parentHasToolbarView

    @SuppressLint("LogNotTimber")
    open fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false, dataBindingComponent)

        view = if (binding == null) {
            Log.d(ArchTreeResource::class.java.name, "Did you forget to define your layout " +
                    "using <layout>...</layout>? Inflating layout using the default " +
                    "LayoutInflater.inflate(...).")
            inflater.inflate(layoutId, container, false)
        } else {
            binding!!.root
        }

        if (binding != null && dataBindingComponentBindingKey != -1) {
            binding!!.setVariable(dataBindingComponentBindingKey, dataBindingComponent)
        }

        return view
    }

    open fun onAttachLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        binding?.lifecycleOwner = lifecycleOwner
        if (lifecycleOwnerBindingKey != -1) binding?.setVariable(lifecycleOwnerBindingKey, lifecycleOwner)
    }
}