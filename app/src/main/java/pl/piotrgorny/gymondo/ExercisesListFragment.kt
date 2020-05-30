package pl.piotrgorny.gymondo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import pl.piotrgorny.gymondo.data.dto.CategoryDto
import pl.piotrgorny.gymondo.data.dto.EquipmentDto
import pl.piotrgorny.gymondo.data.dto.MuscleDto
import pl.piotrgorny.gymondo.databinding.FragmentExercisesListBinding
import java.util.*


class ExercisesListFragment : Fragment() {
    private val viewModel by viewModels<ExercisesListViewModel>(factoryProducer = {
        ExercisesListViewModel.Factory(
            arguments!!.getParcelableArrayList<CategoryDto>("categories")!!.map { it.id to it }.toMap(),
            arguments!!.getParcelableArrayList<MuscleDto>("muscles")!!.map { it.id to it }.toMap(),
            arguments!!.getParcelableArrayList<EquipmentDto>("equipment")!!.map { it.id to it }.toMap()
        )
    })



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentExercisesListBinding>(inflater, R.layout.fragment_exercises_list, container, false)
        binding.viewModel = viewModel
        val adapter = ExercisesAdapter(viewLifecycleOwner)
        binding.exercisesListFragmentExercisesList.adapter = adapter
        binding.exercisesListFragmentExercisesList.layoutManager = LinearLayoutManager(requireContext())
        binding.exercisesListFragmentExercisesList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        viewModel.exercises.observe(viewLifecycleOwner,
            Observer { adapter.submitList(it) })

        return binding.root
    }

    companion object{
        fun newInstance(
            categories: ArrayList<CategoryDto>,
            muscles: ArrayList<MuscleDto>,
            equipment: ArrayList<EquipmentDto>) : ExercisesListFragment {
            val fragment = ExercisesListFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("categories", categories)
            bundle.putParcelableArrayList("muscles", muscles)
            bundle.putParcelableArrayList("equipment", equipment)

            fragment.arguments = bundle

            return fragment
        }
    }
}