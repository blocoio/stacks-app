package io.bloco.circles.domain

import io.bloco.circles.data.AuthRequestModel
import io.bloco.circles.data.IdentityModel
import io.bloco.circles.data.IdentityModel.IdentityAppModel
import io.bloco.circles.data.interfaces.IdentityRepository
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class UpdateWallet
@Inject constructor(
    private val identityRepository: IdentityRepository,
    private val getAppDetails: GetAppDetails,
    private val upWallet: UploadWallet
) {

    suspend fun update(authRequest: AuthRequestModel, identity: IdentityModel) {
        val identities = identityRepository.observe().first().toMutableList()

        val appDetails = getAppDetails.get(authRequest)
        val appModel = identity.appModels.find { it.name == appDetails?.name ?: "" }


        if (appModel != null) {
            Timber.i(appModel.json.toString())
            appModel.json.put(IdentityAppModel.LAST_LOGIN, Date().time)
            Timber.i(appModel.json.toString())
        } else {
            val newModel = JSONObject().apply {
                put(IdentityAppModel.NAME, appDetails?.name ?: authRequest.domainName)
                put(IdentityAppModel.APP_ICON, appDetails?.icon ?: "")
                put(IdentityAppModel.LAST_LOGIN, Date().time)
                put(IdentityAppModel.ORIGIN, authRequest.domainName)
                put(IdentityAppModel.SCOPES, authRequest.scopes)
            }

            val appModels = JSONObject()
            val apps = identity.json.getJSONObject(IdentityModel.APP_MODELS)
            apps.keys().asSequence().map {
                appModels.put(it, apps.get(it))
            }.toList()

            appModels.put(authRequest.domainName, newModel)

            val identityJson = identity.json

            identityJson.put(IdentityModel.APP_MODELS, appModels)


            identities[identities.indexOfFirst { it.username == identity.username }] =
                IdentityModel(
                    identityJson
                )
        }

        Timber.i("New Identities")
        identityRepository.set(identities)
        upWallet.upload(identities)
    }
}
