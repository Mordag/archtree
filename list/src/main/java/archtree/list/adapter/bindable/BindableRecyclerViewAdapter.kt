package archtree.list.adapter.bindable

import androidx.recyclerview.widget.RecyclerView

abstract class BindableRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), BindableListAdapter {

    abstract fun bindRecyclerView(view: RecyclerView)
}