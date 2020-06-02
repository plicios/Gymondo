package pl.piotrgorny.gymondo.data.repository

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.piotrgorny.gymondo.GymondoApplication
import pl.piotrgorny.gymondo.data.dto.*
import pl.piotrgorny.gymondo.data.model.Exercise
import pl.piotrgorny.gymondo.util.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class ExercisesDataSource(
    private val categories: Map<Long, CategoryDto>,
    private val muscles: Map<Long, MuscleDto>,
    private val equipment: Map<Long, EquipmentDto>,
    private val eventLiveData: SingleLiveEvent<Event>,
    private val nameFilter: String?,
    private val categoryFilter: CategoryDto?
) : PageKeyedDataSource<Int, Exercise>() {

    private val wgerService by lazy {
        Retrofit.Builder()
            .baseUrl(GymondoApplication.apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WgerService::class.java)
    }

    private fun handleResponse(response: WgerApiResponse<ExerciseDto>) : List<Exercise> {
        return response.results.filter { it.matchNameFilter(nameFilter) }.map {
                Exercise(
                    it,
                    categories[it.category] ?: throw Exception("No category found for id: ${it.category}"),
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

    private suspend fun makeApiCall(page: Int, pageSize: Int) : Pair<List<Exercise>, Int?> {
        try {
            Timber.e("ApiCall: page:$page, limit:$pageSize, category:${categoryFilter?.id}")
            val response = if(categoryFilter != null){
                wgerService.getExercisesWithCategoryFilter(categoryFilter.id, page, pageSize)
            } else {
                wgerService.getExercises(page, pageSize)
            }
            return if(response.isSuccessful){
                val exercisesList = response.body()?.let { handleResponse(it) } ?: listOf()
                val nextKey = response.body()?.let { getKeyFromUrl(it.next) }
                if(exercisesList.isEmpty() && nextKey != null){
                    Timber.e("additional call")
                    makeApiCall(nextKey, pageSize)
                } else {
                    Pair(exercisesList, nextKey)
                }
            } else {
                throw Exception("Api call was not successful: ${response.errorBody()}")
            }
        } catch (e: Exception) {
            Timber.e(e)
            eventLiveData.postValue(
                ShowApiErrorEvent(
                    "Api call was not successful"
                )
            )
            return Pair(listOf(), null)
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Exercise>
    ) {
        GlobalScope.launch {
            val listWithNextPageKey = makeApiCall(1, params.requestedLoadSize)
            callback.onResult(listWithNextPageKey.first, null, listWithNextPageKey.second)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Exercise>) {
        eventLiveData.postValue(LoadingNextPage())
        GlobalScope.launch {
            val listWithNextPageKey = makeApiCall(params.key, params.requestedLoadSize)
            callback.onResult(listWithNextPageKey.first, listWithNextPageKey.second)
            eventLiveData.postValue(FinishedLoadingNextPage())
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Exercise>) {
    }

    class Factory(
        private val categories: Map<Long, CategoryDto>,
        private val muscles: Map<Long, MuscleDto>,
        private val equipment: Map<Long, EquipmentDto>,
        private val eventLiveData: SingleLiveEvent<Event>,
        var nameFilter: String?,
        var categoryFilter: CategoryDto?
    ) : DataSource.Factory<Long, Exercise>() {
        override fun create(): DataSource<Long, Exercise> {
            return ExercisesDataSource(categories, muscles, equipment, eventLiveData, nameFilter, categoryFilter) as DataSource<Long, Exercise>
        }

    }
}