package pl.piotrgorny.gymondo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.piotrgorny.gymondo.data.model.Exercise
import pl.piotrgorny.gymondo.databinding.ItemExerciseBinding

class ExercisesAdapter(private val lifecycleOwner: LifecycleOwner, private val eventLiveData: SingleLiveEvent<Event>) : PagedListAdapter<Exercise, ExercisesAdapter.ExerciseViewHolder>(Exercise.diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExerciseViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_exercise, parent, false
            )
        )

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = getItem(position)!!
        holder.binding.viewModel = ExerciseViewModel(exercise).also { it.eventLiveData = eventLiveData }
        holder.binding.lifecycleOwner = lifecycleOwner
    }

    class ExerciseViewHolder(val binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root)
}