package pl.piotrgorny.gymondo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import pl.piotrgorny.gymondo.data.dto.CategoryDto
import pl.piotrgorny.gymondo.data.dto.EquipmentDto
import pl.piotrgorny.gymondo.data.dto.MuscleDto
import pl.piotrgorny.gymondo.data.repository.Repository
import pl.piotrgorny.gymondo.data.repository.Result
import pl.piotrgorny.gymondo.databinding.ActivityMainBinding
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding  = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
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

        dataDownloaded.observe(
            this,
            Observer {
                Timber.e("dataDownloaded: $it")
                if(it){
                    binding.dataDownloaded = true
                    supportFragmentManager.beginTransaction()
                        .replace(binding.mainActivityExercisesListFragmentContainer.id, ExercisesListFragment.newInstance(
                            categories.value?.let {
                                val x = arrayListOf<CategoryDto>()
                                x.addAll((it as Result.Success).data)
                                x
                            } ?: arrayListOf(),
                            muscles.value?.let {
                                val x = arrayListOf<MuscleDto>()
                                x.addAll((it as Result.Success).data)
                                x
                            } ?: arrayListOf(),
                            equipment.value?.let {
                                val x = arrayListOf<EquipmentDto>()
                                x.addAll((it as Result.Success).data)
                                x
                            } ?: arrayListOf()
                        ))
                        .commitAllowingStateLoss()
                }
            }
        )



    }
}