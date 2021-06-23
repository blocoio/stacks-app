package io.bloco.circles.domain

import io.bloco.circles.data.IdentityModel
import org.json.JSONObject
import javax.inject.Inject

class GenerateIdentity
@Inject constructor() {

    fun generate(address: String, username: String?): IdentityModel {
        val json = JSONObject().apply {
            val unWrappedUsername = username?.let {
                "$it.id.stx"
            } ?: address

            put(IdentityModel.USERNAME, unWrappedUsername)
            put(IdentityModel.ADDRESS, address)
            put(IdentityModel.APP_MODELS, JSONObject())
        }

        return IdentityModel(json)
    }
}
