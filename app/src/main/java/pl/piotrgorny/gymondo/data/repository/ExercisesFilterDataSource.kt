package pl.piotrgorny.gymondo.data.repository

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.piotrgorny.gymondo.Event
import pl.piotrgorny.gymondo.ShowApiErrorEvent
import pl.piotrgorny.gymondo.SingleLiveEvent
import pl.piotrgorny.gymondo.data.dto.*
import pl.piotrgorny.gymondo.data.model.Exercise
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExercisesFilterDataSource(
    private val muscles: Map<Long, MuscleDto>,
    private val equipment: Map<Long, EquipmentDto>,
    private val category: CategoryDto,
    private val eventLiveData: SingleLiveEvent<Event>
) : PageKeyedDataSource<Int, Exercise>() {

    private val wgerService by lazy {
        Retrofit.Builder()
            .baseUrl("https://wger.de/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WgerService::class.java)
    }

    private fun handleResponse(response: WgerApiResponse<ExerciseDto>) : List<Exercise> {
        return response.results.map {
                Exercise(
                    it,
                    category,
                    it.equipment.map { equipmentId -> equipment[equipmentId] ?: throw Exception("No equipment found for id: $equipmentId")},
                    it.muscles.map { muscleId -> muscles[muscleId] ?: throw Exception("No muscle found for id: $muscleId")}
                )
            }
    }

    private fun getKeyFromUrl(url: String?) : Int? {
        val queryParameters = url?.split('?')?.last()?.split('&')
        val pageQuery = queryParameters?.firstOrNull { it.contains("page") }

        return pageQuery?.filter { it.isDigit() }?.toInt()
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Exercise>
    ) {
        GlobalScope.launch {
            val response = wgerService.getExercisesWithCategoryFilter(category.id)
            if(response.isSuccessful){
                val exercisesList = response.body()?.let { handleResponse(it) } ?: listOf()
                val nextKey = response.body()?.let { getKeyFromUrl(it.next) }
                val previousKey = response.body()?.let { getKeyFromUrl(it.previous) }
                callback.onResult(exercisesList, previousKey, nextKey)
            } else {
                eventLiveData.postValue(ShowApiErrorEvent("Api call was not successful"))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Exercise>) {
        GlobalScope.launch {
            try {
                val response = wgerService.getExercisesWithCategoryFilter(category.id, params.key)
                if(response.isSuccessful){
                    val exercisesList = response.body()?.let { handleResponse(it) } ?: listOf()
                    val nextKey = response.body()?.let { getKeyFromUrl(it.next) }
                    callback.onResult(exercisesList, nextKey)
                } else {
                    eventLiveData.postValue(ShowApiErrorEvent("Api call was not successful"))
                }
            } catch (e: java.lang.Exception){
                eventLiveData.postValue(ShowApiErrorEvent(e.localizedMessage ?: "Other error"))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Exercise>) {
        GlobalScope.launch {
            val response = wgerService.getExercisesWithCategoryFilter(category.id, params.key)
            if(response.isSuccessful){
                val exercisesList = response.body()?.let { handleResponse(it) } ?: listOf()
                val previousKey = response.body()?.let { getKeyFromUrl(it.previous) }
                callback.onResult(exercisesList, previousKey)
            } else {
                eventLiveData.postValue(ShowApiErrorEvent("Api call was not successful"))
            }
        }
    }

    class Factory(
        private val muscles: Map<Long, MuscleDto>,
        private val equipment: Map<Long, EquipmentDto>,
        private val category: CategoryDto,
        private val eventLiveData: SingleLiveEvent<Event>
    ) : DataSource.Factory<Long, Exercise>() {
        override fun create(): DataSource<Long, Exercise> {
            return ExercisesFilterDataSource(muscles, equipment, category, eventLiveData) as DataSource<Long, Exercise>
        }

    }
}