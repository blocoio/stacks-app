package io.bloco.circles.data

import org.json.JSONObject

class IdentityModel(
    val json: JSONObject
) {
    val completeUsername: String? by lazy {
        json.optString(USERNAME)
    }

    val username: String? by lazy {
        json.optString(USERNAME).replace("\\.id.*".toRegex(), "")
    }

    val address: String? by lazy {
        json.optString(ADDRESS)
    }

    val appModels: List<IdentityAppModel> by lazy {
        val apps = json.getJSONObject(APP_MODELS)
        apps.keys().asSequence().map {
            IdentityAppModel(apps.getJSONObject(it))
        }.toList()
    }

    class IdentityAppModel(
        val json: JSONObject
    ) {
        val appIcon: String? by lazy {
            json.optString(APP_ICON)
        }

        val lastLoginAt: Long? by lazy {
            json.optLong(LAST_LOGIN)
        }

        val name: String? by lazy {
            json.optString(NAME)
        }

        val origin: String? by lazy {
            json.optString(ORIGIN)
        }

        val scopes: List<String> by lazy {
            json.optJSONArray(SCOPES)?.let { scopes ->
                List(scopes.length(), scopes::getString)
            } ?: emptyList()
        }

        override fun toString(): String {
            return json.toString()
        }

        companion object {
            const val NAME = "name"
            const val APP_ICON = "appIcon"
            const val LAST_LOGIN = "lastLoginAt"
            const val ORIGIN = "origin"
            const val SCOPES = "scopes"
        }
    }

    companion object {
        const val USERNAME = "username"
        const val ADDRESS = "address"
        const val APP_MODELS = "apps"
    }
}
