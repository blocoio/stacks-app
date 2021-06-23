package io.bloco.circles.domain

import io.bloco.circles.data.network.models.RegistrarName.RegistrarNameStatus
import io.bloco.circles.data.network.services.LookupService
import io.bloco.circles.domain.CheckUsernameStatus.UsernameStatus.*
import retrofit2.HttpException
import javax.inject.Inject

class CheckUsernameStatus
@Inject constructor(
    private val lookupService: LookupService
) {

    //TODO: this needs test coverage
    // we use response code and not status itself
    suspend fun isAvailable(username: String): UsernameStatus = try {
        when(lookupService.lookupName("$username.id.stx").status) {
            RegistrarNameStatus.Available -> Available
            RegistrarNameStatus.InvalidName -> Invalid
            RegistrarNameStatus.SubmittedSubDomain -> Unavailable
            else -> Error
        }
    } catch (e: HttpException) {
        when(e.code()) {
            400 -> Invalid
            404 -> Available
            else -> Error
        }
    } catch (e: Throwable) {
        Error
    }

    enum class UsernameStatus {
        Available, Unavailable, Invalid, Error
    }

}
