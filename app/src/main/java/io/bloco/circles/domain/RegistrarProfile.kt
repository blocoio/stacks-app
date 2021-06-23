package io.bloco.circles.domain

import com.google.gson.Gson
import io.bloco.circles.data.network.models.RegistrarRequest
import io.bloco.circles.data.network.models.RegistrarResponse
import io.bloco.circles.data.network.services.RegistrarService
import javax.inject.Inject

class RegistrarProfile
@Inject constructor(
    private val registrarService: RegistrarService,
    private val gson: Gson
) {
    suspend fun register(username: String, address: String): RegistrarResponse =
        registrarService.register(gson.toJson(buildRequest(username, address)))

    private fun buildRequest(name: String, ownerAddress: String): RegistrarRequest {
        val completeZoneFile =
            "\$ORIGIN $name.id.stx\n" +
                    "\$TTL 3600" +
                    "\n_http._tcp\tIN\tURI\t10\t1\t" +
                    "\"https://gaia.blockstack.org/hub/$ownerAddress/profile.json\"\n\n"

        return RegistrarRequest(name, ownerAddress, completeZoneFile)
    }
}
