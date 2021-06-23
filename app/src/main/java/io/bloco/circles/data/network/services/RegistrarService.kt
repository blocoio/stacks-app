package io.bloco.circles.data.network.services

import io.bloco.circles.data.network.models.RegistrarResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RegistrarService {

    @Headers("Content-Type: application/json")
    @POST("/register")
    suspend fun register(@Body registrarRequest: String): RegistrarResponse
}
