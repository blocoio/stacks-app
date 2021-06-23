package io.bloco.circles.data.network.services

import io.bloco.circles.data.network.models.RegistrarName
import retrofit2.http.GET
import retrofit2.http.Path

interface LookupService {

    @GET("v1/names/{name}")
    suspend fun lookupName(@Path("name") name: String): RegistrarName
}