package pl.piotrgorny.gymondo.data.repository

import pl.piotrgorny.gymondo.data.dto.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WgerService {

    @GET("exercise")
    suspend fun getExercises(@Query("page") page: Int = 1) : Response<WgerApiResponse<ExerciseDto>>

    @GET("exercise")
    suspend fun getExercisesWithCategoryFilter(@Query("category") categoryId: Long, @Query("page") page: Int = 1) : Response<WgerApiResponse<ExerciseDto>>

    @GET("exercisecategory")
    suspend fun getCategories(@Query("limit") limit: Int = Int.MAX_VALUE) : Response<WgerApiResponse<CategoryDto>>

    @GET("muscle")
    suspend fun getMuscles(@Query("limit") limit: Int = Int.MAX_VALUE) : Response<WgerApiResponse<MuscleDto>>

    @GET("equipment")
    suspend fun getEquipment(@Query("limit") limit: Int = Int.MAX_VALUE) : Response<WgerApiResponse<EquipmentDto>>

    @GET("exerciseimage")
    suspend fun getImages(@Query("exercise") exerciseId: Long, @Query("limit") limit: Int = Int.MAX_VALUE) : Response<WgerApiResponse<ImageDto>>
}