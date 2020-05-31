package pl.piotrgorny.gymondo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.observe
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.findNavController
import pl.piotrgorny.gymondo.data.repository.Repository
import pl.piotrgorny.gymondo.data.repository.Result
import pl.piotrgorny.gymondo.databinding.FragmentInitialDataDownloadBinding
import timber.log.Timber


class InitialDataDownloadFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentInitialDataDownloadBinding>(inflater, R.layout.fragment_initial_data_download, container, false)
        binding.dataDownloaded = false

        val repository = Repository()
        val muscles = repository.getMuscles()
        val equipment = repository.getEquipment()
        val categories = repository.getCategories()

        val musclesDownloaded = Transformations.map(muscles) {
            Timber.e(it.toString())
            it is Result.Success
        }
        val equipmentDownloaded = Transformations.map(equipment) {
            Timber.e(it.toString())
            it is Result.Success
        }
        val categoriesDownloaded = Transformations.map(categories) {
            Timber.e(it.toString())
            it is Result.Success
        }

        val dataDownloaded = MediatorLiveData<Boolean>()
        dataDownloaded.addSource(musclesDownloaded){
            dataDownloaded.postValue(
                musclesDownloaded.value ?: false &&
                        equipmentDownloaded.value ?: false &&
                        categoriesDownloaded.value ?: false
            )
        }
        dataDownloaded.addSource(equipmentDownloaded){
            dataDownloaded.postValue(
                musclesDownloaded.value ?: false &&
                        equipmentDownloaded.value ?: false &&
                        categoriesDownloaded.value ?: false
            )
        }
        dataDownloaded.addSource(categoriesDownloaded){
            dataDownloaded.postValue(
                musclesDownloaded.value ?: false &&
                        equipmentDownloaded.value ?: false &&
                        categoriesDownloaded.value ?: false
            )
        }

        dataDownloaded.observe(viewLifecycleOwner)
            { allDataDownloaded ->
                Timber.e("dataDownloaded: $allDataDownloaded")
                if(allDataDownloaded){
                    binding.dataDownloaded = true
                    findNavController().navigate(R.id.action_initial_data_download_dest_to_exercises_list_dest, ExercisesListFragment.args(
                        categories.value?.let {
                            (it as Result.Success).data.toTypedArray()
                        } ?: arrayOf(),
                        muscles.value?.let {
                            (it as Result.Success).data.toTypedArray()
                        } ?: arrayOf(),
                        equipment.value?.let {
                            (it as Result.Success).data.toTypedArray()
                        } ?: arrayOf()
                    ))
                }
            }


        return binding.root
    }
}