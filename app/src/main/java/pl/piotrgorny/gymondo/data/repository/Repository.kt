package pl.piotrgorny.gymondo.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import pl.piotrgorny.gymondo.ShowApiErrorEvent
import pl.piotrgorny.gymondo.data.dto.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


class Repository {
    private val wgerService by lazy {
        Retrofit.Builder()
            .baseUrl("https://wger.de/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WgerService::class.java)
    }

    fun getImages(exerciseId: Long) : LiveData<List<ImageDto>> = liveData {
        try {
            val response = wgerService.getImages(exerciseId)

            if(!response.isSuccessful) {
                emit(listOf<ImageDto>())
                return@liveData
            }

            val responseBody = response.body()
            if(responseBody == null){
                emit(listOf<ImageDto>())
                return@liveData
            }

            if(responseBody.next != null) {
                emit(listOf<ImageDto>())
                return@liveData
            }

            emit(responseBody.results)
        } catch (e: java.lang.Exception){
            Timber.e(e)
            //TODO add try catch to all functions and pass error to Screen
//            eventLiveData.postValue(ShowApiErrorEvent(e.localizedMessage ?: "Other error"))
        }
    }

    fun getMuscles() : LiveData<Result<List<MuscleDto>>> = liveData {
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
    }

    fun getEquipment() : LiveData<Result<List<EquipmentDto>>> = liveData {
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
    }

    fun getCategories() : LiveData<Result<List<CategoryDto>>> = liveData {
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
    }
}