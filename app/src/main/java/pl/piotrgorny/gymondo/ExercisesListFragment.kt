package pl.piotrgorny.gymondo

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import pl.piotrgorny.gymondo.data.dto.CategoryDto
import pl.piotrgorny.gymondo.data.dto.EquipmentDto
import pl.piotrgorny.gymondo.data.dto.MuscleDto
import pl.piotrgorny.gymondo.databinding.FragmentExercisesListBinding


class ExercisesListFragment : Fragment() {
    private val eventLiveData = SingleLiveEvent<Event>()

    private val viewModel by viewModels<ExercisesListViewModel>(factoryProducer = {
        ExercisesListViewModel.Factory(
            arguments!!.getParcelableArray("categories")!!.map { (it as CategoryDto).id to it }.toMap(),
            arguments!!.getParcelableArray("muscles")!!.map { (it as MuscleDto).id to it }.toMap(),
            arguments!!.getParcelableArray("equipment")!!.map { (it as EquipmentDto).id to it }.toMap(),
            eventLiveData
        )
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentExercisesListBinding>(inflater, R.layout.fragment_exercises_list, container, false)
        binding.viewModel = viewModel

        eventLiveData.observe(viewLifecycleOwner) {
            when(it){
                is ShowExerciseDetailsEvent ->
                    findNavController().navigate(R.id.action_exercises_list_dest_to_exercise_dest, ExerciseFragment.args(it.exercise))
                is ShowApiErrorEvent ->
                    AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage(it.errorText)
                        .setNeutralButton("Ok") {
                            dialog, which ->
                                dialog.dismiss()
                        }
                        .show()
            }
        }
        val adapter = ExercisesAdapter(viewLifecycleOwner, eventLiveData)
        binding.exercisesListFragmentExercisesList.adapter = adapter
        binding.exercisesListFragmentExercisesList.layoutManager = LinearLayoutManager(requireContext())
        binding.exercisesListFragmentExercisesList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        viewModel.exercises.observe(viewLifecycleOwner) { adapter.submitList(it) }

        return binding.root
    }

    companion object{
        fun args(
            categories: Array<CategoryDto>,
            muscles: Array<MuscleDto>,
            equipment: Array<EquipmentDto>
        ) = Bundle().also {
            it.putParcelableArray("categories", categories)
            it.putParcelableArray("muscles", muscles)
            it.putParcelableArray("equipment", equipment)
        }
    }
}