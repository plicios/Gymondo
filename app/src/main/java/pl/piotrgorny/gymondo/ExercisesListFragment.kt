package pl.piotrgorny.gymondo

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
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

    private val categories by lazy {
        arguments!!.getParcelableArray(categoriesArg)!!.map { (it as CategoryDto).id to it }.toMap()
    }

    private val muscles by lazy {
        arguments!!.getParcelableArray(musclesArg)!!.map { (it as MuscleDto).id to it }.toMap()
    }

    private val equipment by lazy {
        arguments!!.getParcelableArray(equipmentArg)!!.map { (it as EquipmentDto).id to it }.toMap()
    }

    private val viewModel by viewModels<ExercisesListViewModel>(factoryProducer = {
        ExercisesListViewModel.Factory(
            categories,
            muscles,
            equipment,
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
                        .setTitle(R.string.error_dialog_title)
                        .setMessage(it.errorText)
                        .setNeutralButton(R.string.error_dialog_button_title) {
                                dialog, _ ->
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

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.list_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)

        val searchItem = menu.findItem(R.id.search_list)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.nameFilter.postValue(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.nameFilter.postValue(newText)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.filter_list -> {
                val categoriesToFilter = categories.values.map { it.name }.toTypedArray()
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.filter_dialog_title)
                    .setItems(categoriesToFilter){
                        dialog, which ->
                            viewModel.categoryFilter.postValue(categories.values.first { it.name == categoriesToFilter[which] })
                            dialog.dismiss()
                    }
                    .setNeutralButton(R.string.clear_filter_text) {
                        dialog, _ ->
                            viewModel.categoryFilter.postValue(null)
                            dialog.dismiss()
                    }
                    .create()
                    .show()
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    companion object{
        const val categoriesArg = "categories"
        const val musclesArg = "muscles"
        const val equipmentArg = "equipment"

        fun args(
            categories: Array<CategoryDto>,
            muscles: Array<MuscleDto>,
            equipment: Array<EquipmentDto>
        ) = Bundle().also {
            it.putParcelableArray(categoriesArg, categories)
            it.putParcelableArray(musclesArg, muscles)
            it.putParcelableArray(equipmentArg, equipment)
        }
    }
}