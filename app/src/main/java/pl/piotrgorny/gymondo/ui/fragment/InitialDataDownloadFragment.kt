package pl.piotrgorny.gymondo.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.observe
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.findNavController
import pl.piotrgorny.gymondo.util.Event
import pl.piotrgorny.gymondo.R
import pl.piotrgorny.gymondo.util.ShowApiErrorEvent
import pl.piotrgorny.gymondo.util.SingleLiveEvent
import pl.piotrgorny.gymondo.data.repository.ExerciseInfoRepository
import pl.piotrgorny.gymondo.data.Result


class InitialDataDownloadFragment : Fragment() {

    private val repository by lazy { ExerciseInfoRepository() }
    private val muscles by lazy { repository.getMuscles() }
    private val equipment by lazy { repository.getEquipment() }
    private val categories by lazy { repository.getCategories() }

    private val downloadingDataEvents by lazy { SingleLiveEvent<Event>() }


    override fun onCreateView(inflater: LayoutInflater,
                          container: ViewGroup?,
                          savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_initial_data_download, container, false)

        observeForErrors()
        downloadInitialData()

        return view
    }

    private fun downloadInitialData(){
        val musclesDownloaded = Transformations.map(muscles) {
            it is Result.Success
        }
        val equipmentDownloaded = Transformations.map(equipment) {
            it is Result.Success
        }
        val categoriesDownloaded = Transformations.map(categories) {
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
        {
            if(it){
                afterDataDownloaded()
            }
        }
    }

    private fun observeForErrors() {
        downloadingDataEvents.observe(viewLifecycleOwner){
            if(it is ShowApiErrorEvent){
                showErrorDialog(it.errorText)
            }
        }

        muscles.observe(viewLifecycleOwner){
            if(it is Result.Error){
                downloadingDataEvents.postValue(
                    ShowApiErrorEvent(
                        getString(R.string.api_connection_error)
                    )
                )
            }
        }

        equipment.observe(viewLifecycleOwner){
            if(it is Result.Error){
                downloadingDataEvents.postValue(
                    ShowApiErrorEvent(
                        getString(R.string.api_connection_error)
                    )
                )
            }
        }

        categories.observe(viewLifecycleOwner){
            if(it is Result.Error){
                downloadingDataEvents.postValue(
                    ShowApiErrorEvent(
                        getString(R.string.api_connection_error)
                    )
                )
            }
        }
    }

    private fun afterDataDownloaded(){
        findNavController().navigate(
            R.id.action_initial_data_download_dest_to_exercises_list_dest, ExercisesListFragment.args(
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

    private fun showErrorDialog(message: String){
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_dialog_title)
            .setMessage(message) // R.string.api_connection_error
            .setNeutralButton(R.string.error_dialog_button_title){
                dialog, _ ->
                    downloadInitialData()
                    dialog.dismiss()
            }
            .create()
            .show()
    }
}