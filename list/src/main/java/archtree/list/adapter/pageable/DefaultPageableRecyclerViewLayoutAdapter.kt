package archtree.list.adapter.pageable

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import archtree.list.item.BindableListItem
import archtree.list.item.DataContextAwareViewHolder
import java.lang.ref.WeakReference

open class DefaultPageableRecyclerViewLayoutAdapter(private val context: Context) : PageableRecyclerViewAdapter() {

    private var itemLayout: Int = 0
    private var viewModel: ViewModel? = null
    private var dataBindingComponent: Any? = null
    private var lifecycleOwner: LifecycleOwner? = null

    private var dataBindingComponentKey: Int? = null
    private var lifecycleOwnerKey: Int? = null

    private var recyclerViewRef = WeakReference<RecyclerView?>(null)

    override fun bindRecyclerView(view: RecyclerView) {
        recyclerViewRef = WeakReference(view)
    }

    override fun onUpdate(list: PagedList<BindableListItem>, @LayoutRes itemLayout: Int?, viewModel: ViewModel?,
                          dataBindingComponent: Any?, dataBindingComponentKey: Int?,
                          lifecycleOwner: LifecycleOwner?, lifecycleOwnerKey: Int?) {

        this.itemLayout = itemLayout ?: 0
        this.viewModel = viewModel
        this.dataBindingComponent = dataBindingComponent
        this.lifecycleOwner = lifecycleOwner
        this.dataBindingComponentKey = dataBindingComponentKey
        this.lifecycleOwnerKey = lifecycleOwnerKey

        submitList(list)
        recyclerViewRef.get()?.scheduleLayoutAnimation()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //use the first item in list to determine the correct item layout that should be used
        val itemLayoutRes = getItem(0)?.onDetermineLayoutRes(viewType) ?: itemLayout

        val realDataBindingComponent: DataBindingComponent? = if (dataBindingComponent != null) {
            try {
                dataBindingComponent as? DataBindingComponent?
            } catch (e: ClassCastException) {
                null
            }
        } else null

        val binding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(context),
                itemLayoutRes,
                parent,
                false,
                realDataBindingComponent
        )

        val dataBindingKey = dataBindingComponentKey
        if (dataBindingKey != null) binding.setVariable(dataBindingKey, realDataBindingComponent)

        val lifecycleKey = lifecycleOwnerKey
        if (lifecycleKey != null) binding.setVariable(lifecycleKey, lifecycleOwner)

        if (lifecycleOwner != null) binding.lifecycleOwner = lifecycleOwner

        return DataContextAwareViewHolder(binding, viewType)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null && viewHolder is DataContextAwareViewHolder) viewHolder.onBind(item, viewModel)
    }

    override fun getItemViewType(position: Int): Int = getItem(position)?.getItemViewType() ?: 0
}