package pl.piotrgorny.gymondo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import pl.piotrgorny.gymondo.data.model.Exercise
import pl.piotrgorny.gymondo.databinding.FragmentExerciseBinding


class ExerciseFragment : Fragment() {
    private val viewModel by viewModels<ExerciseViewModel>(factoryProducer = {
        ExerciseViewModel.Factory(arguments!!.getParcelable("exercise")!!)
    })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentExerciseBinding>(inflater, R.layout.fragment_exercise, container, false)
        binding.viewModel = viewModel
        val adapter = ImagesAdapter()
        binding.exerciseFragmentImages.adapter = adapter

        viewModel.images.observe(viewLifecycleOwner) { images ->
            adapter.items = images.map { it.image }
        }

        return binding.root
    }

    companion object{
        fun args(exercise: Exercise) : Bundle {
            return bundleOf("exercise" to exercise)
        }
    }
}