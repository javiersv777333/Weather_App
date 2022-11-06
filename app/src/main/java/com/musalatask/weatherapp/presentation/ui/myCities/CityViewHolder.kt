package com.musalatask.weatherapp.presentation.ui.myCities

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.musalatask.weatherapp.databinding.ViewHolderCityBinding

class CityViewHolder(
    val deleteAction: (String) -> Unit, view: View,
    private val selectAction: (String) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val binding: ViewHolderCityBinding = ViewHolderCityBinding.bind(view)

    fun bind(cityName: String) {
        binding.cityName.text = cityName
        binding.imageView.setOnClickListener { deleteAction.invoke(cityName) }
        itemView.setOnClickListener { selectAction.invoke(cityName) }
    }
}