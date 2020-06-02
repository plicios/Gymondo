package pl.piotrgorny.gymondo

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import pl.piotrgorny.gymondo.data.dto.CategoryDto
import pl.piotrgorny.gymondo.data.dto.EquipmentDto
import pl.piotrgorny.gymondo.data.dto.MuscleDto
import pl.piotrgorny.gymondo.data.model.Exercise
import pl.piotrgorny.gymondo.data.repository.ExercisesDataSource

class ExercisesListViewModel(categories: Map<Long, CategoryDto>, muscles: Map<Long, MuscleDto>, equipment: Map<Long, EquipmentDto>, eventLiveData: SingleLiveEvent<Event>) : ViewModel() {
    val categoryFilter = MutableLiveData<CategoryDto?>()
    val nameFilter = MutableLiveData<String?>()

    val exercises: LiveData<PagedList<Exercise>>

    private val dataSourceFactory = ExercisesDataSource.Factory(categories, muscles, equipment, eventLiveData, null, null)

    init {
        val pageSize = 20
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize) //Initial page size needs to be the same as page size for because Api is queried by limit and page (data duplicated in two queries if page size is smaller then initial page size)
            .build()

        exercises = LivePagedListBuilder(dataSourceFactory, config).build()
        categoryFilter.observeForever {
            dataSourceFactory.categoryFilter = it
            exercises.value?.dataSource?.invalidate()
        }
        nameFilter.observeForever {
            dataSourceFactory.nameFilter = it
            exercises.value?.dataSource?.invalidate()
        }
    }

    class Factory(
        private val categories: Map<Long, CategoryDto>,
        private val muscles: Map<Long, MuscleDto>,
        private val equipment: Map<Long, EquipmentDto>,
        private val eventLiveData: SingleLiveEvent<Event>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExercisesListViewModel(categories, muscles, equipment, eventLiveData) as T
        }
    }
}