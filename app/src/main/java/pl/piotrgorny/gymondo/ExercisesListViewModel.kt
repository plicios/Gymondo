package pl.piotrgorny.gymondo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import pl.piotrgorny.gymondo.data.dto.CategoryDto
import pl.piotrgorny.gymondo.data.dto.EquipmentDto
import pl.piotrgorny.gymondo.data.dto.MuscleDto
import pl.piotrgorny.gymondo.data.model.Exercise
import pl.piotrgorny.gymondo.data.repository.ExercisesDataSource

class ExercisesListViewModel(categories: Map<Long, CategoryDto>, muscles: Map<Long, MuscleDto>, equipment: Map<Long, EquipmentDto>) : ViewModel() {
    val exercises: LiveData<PagedList<Exercise>>

    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(4)
            .setPageSize(20)
            .build()
        exercises = LivePagedListBuilder(ExercisesDataSource.Factory(categories, muscles, equipment), config).build()
    }

    class Factory(
        val categories: Map<Long, CategoryDto>,
        val muscles: Map<Long, MuscleDto>,
        val equipment: Map<Long, EquipmentDto>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExercisesListViewModel(categories, muscles, equipment) as T
        }
    }
}