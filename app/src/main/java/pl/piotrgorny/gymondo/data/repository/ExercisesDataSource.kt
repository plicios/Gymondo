package pl.piotrgorny.gymondo.data.repository

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import pl.piotrgorny.gymondo.data.dto.*
import pl.piotrgorny.gymondo.data.model.Exercise
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExercisesDataSource(
    var categories: Map<Long, CategoryDto>,
    var muscles: Map<Long, MuscleDto>,
    var equipment: Map<Long, EquipmentDto>
) : PageKeyedDataSource<Int, Exercise>() {

    private val wgerService by lazy {
        Retrofit.Builder()
            .baseUrl("https://wger.de/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WgerService::class.java)
    }

    fun handleResponse(response: WgerApiResponse<ExerciseDto>) : List<Exercise> {
        return response.results.map {
                Exercise(
                    it,
                    categories[it.category] ?: throw Exception("No category found for id: ${it.category}"),
                    it.equipment.map { equipmentId -> equipment[equipmentId] ?: throw Exception("No equipment found for id: $equipmentId")},
                    it.muscles.map { muscleId -> muscles[muscleId] ?: throw Exception("No muscle found for id: $muscleId")}
                )
            }
    }

    fun getKeyFromUrl(url: String?) : Int? {
        return url?.split('/')?.last()?.filter { it.isDigit() }?.toInt()
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Exercise>
    ) {
        val exercisesCall = wgerService.getExercises()
        exercisesCall.enqueue(object: Callback<WgerApiResponse<ExerciseDto>>{
            override fun onResponse(
                call: Call<WgerApiResponse<ExerciseDto>>,
                response: Response<WgerApiResponse<ExerciseDto>>
            ) {
                if(response.isSuccessful){
                    val exercisesList = response.body()?.let { handleResponse(it) } ?: listOf()
                    val nextKey = response.body()?.let { getKeyFromUrl(it.next) }
                    val previousKey = response.body()?.let { getKeyFromUrl(it.previous) }
                    callback.onResult(exercisesList, previousKey, nextKey)
                }
            }

            override fun onFailure(call: Call<WgerApiResponse<ExerciseDto>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Exercise>) {
        val exercisesCall = wgerService.getExercises(params.key)
        exercisesCall.enqueue(object: Callback<WgerApiResponse<ExerciseDto>>{
            override fun onResponse(
                call: Call<WgerApiResponse<ExerciseDto>>,
                response: Response<WgerApiResponse<ExerciseDto>>
            ) {
                if(response.isSuccessful){
                    val exercisesList = response.body()?.let { handleResponse(it) } ?: listOf()
                    val nextKey = response.body()?.let { getKeyFromUrl(it.next) }
                    callback.onResult(exercisesList, nextKey)
                }
            }

            override fun onFailure(call: Call<WgerApiResponse<ExerciseDto>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Exercise>) {
        val exercisesCall = wgerService.getExercises(params.key)
        exercisesCall.enqueue(object: Callback<WgerApiResponse<ExerciseDto>>{
            override fun onResponse(
                call: Call<WgerApiResponse<ExerciseDto>>,
                response: Response<WgerApiResponse<ExerciseDto>>
            ) {
                if(response.isSuccessful){
                    val exercisesList = response.body()?.let { handleResponse(it) } ?: listOf()
                    val previousKey = response.body()?.let { getKeyFromUrl(it.previous) }
                    callback.onResult(exercisesList, previousKey)
                }
            }

            override fun onFailure(call: Call<WgerApiResponse<ExerciseDto>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    class Factory(
        var categories: Map<Long, CategoryDto>,
        var muscles: Map<Long, MuscleDto>,
        var equipment: Map<Long, EquipmentDto>
    ) : DataSource.Factory<Long, Exercise>() {
        override fun create(): DataSource<Long, Exercise> {
            return ExercisesDataSource(categories, muscles, equipment) as DataSource<Long, Exercise>
        }

    }
}