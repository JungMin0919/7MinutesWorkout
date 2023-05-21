package com.example.a7minutesworkout

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minutesworkout.databinding.ItemExerciseStatusBinding

class ExerciseStatusAdapter(val items: ArrayList<ExerciseModel>): RecyclerView.Adapter<ExerciseStatusAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemExerciseStatusBinding): RecyclerView.ViewHolder(binding.root){
        val tvItem = binding.tvItem

        fun bindItem(model: ExerciseModel){
            tvItem.text = model.id.toString()

            when{
                model.isSelected ->{
                    tvItem.background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.item_cirular_thin_color_accent_border)
                    tvItem.setTextColor(Color.parseColor("#212121"))
                }
                model.isCompleted ->{
                    Log.e("test", "tt")
                    tvItem.background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.item_circular_color_accent_background)
                    tvItem.setTextColor(Color.parseColor("#ffffff"))
                }
                else ->{
                    tvItem.background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.item_cirular_color_gray_background)
                    tvItem.setTextColor(Color.parseColor("#000000"))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemExerciseStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: ExerciseModel = items[position]
        holder.bindItem(model)
    }
}