package archtree.fragment

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import archknife.annotation.util.Injectable
import archtree.ArchTreeResource
import archtree.viewmodel.BaseViewModel
import javax.inject.Inject

abstract class ArchTreeFragment<ViewModel : BaseViewModel> : Fragment(), Injectable,
        HasFragmentBuilder<ViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var fragmentResource: FragmentResource<ViewModel>? = null
        private set

    var menu: Menu? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val builder = FragmentBuilder<ViewModel>()
        fragmentResource = provideFragmentResource(builder)

        val hasOptionsMenu = fragmentResource?.hasOptionsMenu
        if (hasOptionsMenu != null) setHasOptionsMenu(hasOptionsMenu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return fragmentResource?.onCreateView(inflater, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentResource?.onCreateViewModel(this, viewModelFactory)
        fragmentResource?.onAttachLifecycleOwner(this)

        if (!isHidden) {
            refreshFragmentToolbar(activity, view, fragmentResource)
            fragmentResource?.onInitViewModel(this, savedInstanceState)
        }

        fragmentResource?.layer?.onCreate(getViewModel(), savedInstanceState)
    }

    open fun onBackPressed(): Boolean {
        return getViewModel()?.onBackPressed() ?: false
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            refreshFragmentToolbar(activity, view, fragmentResource)

            //if the viewmodel was not initialised during onCreate, the onResume will try again
            if(getViewModel() != null && getViewModel()?.isInitialised == false) {
                fragmentResource?.onInitViewModel(this, null)
            }

            fragmentResource?.layer?.onResume(getViewModel())
        }
    }

    private fun refreshFragmentToolbar(activity: Activity?, rootView: View?, fragmentResource: ArchTreeResource<*>?) {
        val toolbarViewId = fragmentResource?.toolbarViewId
        val toolbarTitle = fragmentResource?.toolbarTitle

        if (toolbarViewId != null && activity != null && activity is AppCompatActivity) {
            if (fragmentResource.parentHasToolbarView) activity.setSupportActionBar(activity.findViewById(toolbarViewId))
            else activity.setSupportActionBar(rootView?.findViewById(toolbarViewId))

            val displayHomeAsUpEnabled = fragmentResource.displayHomeAsUpEnabled
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled)

            if (toolbarTitle != null) activity.supportActionBar?.title = toolbarTitle

            val toolbarIcon = fragmentResource.toolbarIcon
            if (toolbarIcon != null) activity.supportActionBar?.setIcon(toolbarIcon)
        } else if (toolbarTitle != null) activity?.title = toolbarTitle
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        getViewModel()?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        fragmentResource?.layer?.onStart(getViewModel())
    }

    override fun onStop() {
        super.onStop()
        fragmentResource?.layer?.onStop(getViewModel())
    }

    override fun onPause() {
        super.onPause()
        fragmentResource?.layer?.onPause(getViewModel())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentResource?.layer?.onDestroy(getViewModel())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        createFragmentMenu(menu, inflater)
    }

    open fun onFragmentCreateOptionsMenu(menu: Menu?): Boolean {
        return createFragmentMenu(menu, activity?.menuInflater)
    }

    private fun createFragmentMenu(menu: Menu?, inflater: MenuInflater?): Boolean {
        this.menu = menu
        menu?.clear() //ensure that the menu is cleared before inflating it for this fragment

        val menuId = fragmentResource?.menuId
        if (menuId != null) inflater?.inflate(menuId, menu)

        return getViewModel()?.onCreateOptionsMenu(menu) ?: false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return getViewModel()?.onOptionsItemSelected(item) ?: false
    }

    open fun onFragmentActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return getViewModel()?.onActivityResult(requestCode, resultCode, data) ?: false
    }

    open fun onFragmentRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {
        return getViewModel()?.onRequestPermissionsResult(requestCode, permissions, grantResults)
                ?: false
    }

    open fun onFragmentConfigurationChanged(newConfig: Configuration): Boolean {
        return getViewModel()?.onConfigurationChanged(newConfig) ?: false
    }

    open fun onFragmentNewIntent(intent: Intent?): Boolean {
        return getViewModel()?.onNewIntent(intent) ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getViewModel()?.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        getViewModel()?.onRestoreInstanceState(savedInstanceState)
        super.onViewStateRestored(savedInstanceState)
    }

    open fun getViewModel(): ViewModel? {
        return fragmentResource?.viewModel
    }

    open fun getBinding(): ViewDataBinding? {
        return fragmentResource?.binding
    }

    open fun getCustomBundle(): Bundle? {
        return fragmentResource?.customBundle
    }
}