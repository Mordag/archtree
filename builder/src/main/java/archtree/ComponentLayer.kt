package archtree

import android.os.Bundle
import archtree.viewmodel.BaseViewModel

abstract class ComponentLayer<in ViewModel : BaseViewModel> {

    open fun onResume(viewModel: ViewModel?) {
        //do nothing by default
    }

    open fun onCreate(viewModel: ViewModel?, savedInstanceState: Bundle?) {
        //do nothing by default
    }

    open fun onStart(viewModel: ViewModel?) {
        //do nothing by default
    }

    open fun onStop(viewModel: ViewModel?) {
        //do nothing by default
    }

    open fun onPause(viewModel: ViewModel?) {
        //do nothing by default
    }

    open fun onDestroy(viewModel: ViewModel?) {
        //do nothing by default
    }
}