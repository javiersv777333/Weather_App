package com.musalatask.weatherapp.presentation.ui.myCities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.musalatask.weatherapp.R

/**
 * Adapter used to populate the recycler view of cities screen.
 *
 * @param[deleteAction] action to execute if the user touch the remove
 * icon.
 * @param[selectAction] action to execute if the user select a city.
 */
class MyCitiesAdapter(
    private val deleteAction: (String) -> Unit,
    private val selectAction: (String) -> Unit
) :
    ListAdapter<String, CityViewHolder>(Comparator()) {

    class Comparator : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_holder_city, parent, false)
        return CityViewHolder(deleteAction = deleteAction, selectAction = selectAction, view = view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}