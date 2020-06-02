package pl.piotrgorny.gymondo.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import pl.piotrgorny.gymondo.GymondoApplication
import pl.piotrgorny.gymondo.data.Result
import pl.piotrgorny.gymondo.data.dto.CategoryDto
import pl.piotrgorny.gymondo.data.dto.EquipmentDto
import pl.piotrgorny.gymondo.data.dto.ImageDto
import pl.piotrgorny.gymondo.data.dto.MuscleDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


class ExerciseInfoRepository {
    private val wgerService by lazy {
        Retrofit.Builder()
            .baseUrl(GymondoApplication.apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WgerService::class.java)
    }

    fun getImages(exerciseId: Long) : LiveData<List<ImageDto>> = liveData {
        try {
            val response = wgerService.getImages(exerciseId)

            if(!response.isSuccessful) {
                throw Exception()
            }

            val responseBody = response.body() ?: throw Exception()

            if(responseBody.next != null) {
                throw Exception()
            }

            emit(responseBody.results)
        } catch (e: Exception){
            Timber.e(e)
            emit(listOf(errorImage))
        }
    }

    private val errorImage = ImageDto(0,"", "", "error",true, 0, 0)

    fun getMuscles() : LiveData<Result<List<MuscleDto>>> = liveData {
        try {
            val response = wgerService.getMuscles()
            if(!response.isSuccessful) {
                emit(Result.Error(Exception("Api call failed: ${response.errorBody()}")))
                return@liveData
            }

            val responseBody = response.body()
            if(responseBody == null){
                emit(Result.Error(Exception("No body received from Api call")))
                return@liveData
            }

            if(responseBody.next != null) {
                emit(Result.Error(IllegalStateException("Not all muscles fit to one request")))
                return@liveData
            }

            emit(Result.Success(responseBody.results))
        } catch (e: Exception){
            Timber.e(e)
            emit(Result.Error(Exception("Api call failed")))
        }
    }

    fun getEquipment() : LiveData<Result<List<EquipmentDto>>> = liveData {
        try{
            val response = wgerService.getEquipment()
            if(!response.isSuccessful) {
                emit(Result.Error(Exception("Api call failed")))
                return@liveData
            }

            val responseBody = response.body()
            if(responseBody == null){
                emit(Result.Error(Exception("No body received from Api call")))
                return@liveData
            }

            if(responseBody.next != null) {
                emit(Result.Error(IllegalStateException("Not all equipment fit to one request")))
                return@liveData
            }

            emit(Result.Success(responseBody.results))
        } catch (e: Exception){
            Timber.e(e)
            emit(Result.Error(Exception("Api call failed")))
        }
    }

    fun getCategories() : LiveData<Result<List<CategoryDto>>> = liveData {
        try{
            val response = wgerService.getCategories()
            if(!response.isSuccessful) {
                emit(Result.Error(Exception("Api call failed")))
                return@liveData
            }

            val responseBody = response.body()
            if(responseBody == null){
                emit(Result.Error(Exception("No body received from Api call")))
                return@liveData
            }

            if(responseBody.next != null) {
                emit(Result.Error(IllegalStateException("Not all categories fit to one request")))
                return@liveData
            }

            emit(Result.Success(responseBody.results))
        } catch (e: Exception){
            Timber.e(e)
            emit(Result.Error(Exception("Api call failed")))
        }
    }
}