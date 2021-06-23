package io.bloco.circles

import android.content.ClipboardManager
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
import androidx.security.crypto.MasterKey
import com.tfcporciuncula.flow.FlowSharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.bloco.circles.data.AuthRequestsStore
import org.blockstack.android.sdk.Blockstack
import org.blockstack.android.sdk.model.Hub
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CirclesModule {

    @Provides
    @Singleton
    fun keyGenParameterSpec(): KeyGenParameterSpec {
        val builder = KeyGenParameterSpec.Builder(
            CIRCLE_MASTER_KEY_ALIAS,
            PURPOSE_ENCRYPT or PURPOSE_DECRYPT
        )

        builder.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)

        return builder.build()
    }

    @Provides
    @Singleton
    fun masterKey(
        @ApplicationContext appContext: Context,
        keyGenParameterSpec: KeyGenParameterSpec
    ): MasterKey =
        MasterKey.Builder(appContext, CIRCLE_MASTER_KEY_ALIAS)
            .setKeyGenParameterSpec(keyGenParameterSpec)
            .build()

    @Provides
    @Singleton
    fun flowSharedPreferences(
        @ApplicationContext appContext: Context,
        masterKey: MasterKey
    ): FlowSharedPreferences = FlowSharedPreferences(
        EncryptedSharedPreferences.create(
            appContext,
            PREFERENCES_FILE_NAME,
            masterKey,
            AES256_SIV,
            AES256_GCM
        )
    )

    @Provides
    @Singleton
    fun blockstack() =
        Blockstack()

    @Provides
    fun hub() =
        Hub()

    @Provides
    @Singleton
    fun authRequestStore() =
        AuthRequestsStore()

    @Provides
    @Singleton
    fun clipboardManager(@ApplicationContext context: Context) =
        ContextCompat.getSystemService(context, ClipboardManager::class.java) as ClipboardManager

    companion object {
        const val KEY_SIZE = 256
        const val AUTH_VALIDITY_SECONDS = 5
        const val CIRCLE_MASTER_KEY_ALIAS = "_circle_security_master_key_"
        const val PREFERENCES_FILE_NAME = "encrypted_preferences"
    }
}
