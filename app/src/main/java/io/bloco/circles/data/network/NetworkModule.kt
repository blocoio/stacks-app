package io.bloco.circles.data.network

import android.app.Application
import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.bloco.circles.BuildConfig
import io.bloco.circles.R
import io.bloco.circles.data.network.models.RegistrarName
import io.bloco.circles.data.network.models.RegistrarNameStatusAdapter
import io.bloco.circles.data.network.services.*
import me.sianaki.flowretrofitadapter.FlowCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun resources(application: Application) = application.resources

    @Singleton
    @Provides
    fun gson() =
        GsonBuilder()
            .registerTypeAdapter(
                RegistrarName.RegistrarNameStatus::class.java,
                RegistrarNameStatusAdapter()
            )
            .create()

    @Singleton
    @Provides
    @GaiaBaseUrl
    fun gaiaBaseUrl(resources: Resources) = resources.getString(R.string.gaia_endpoint)

    @Singleton
    @Provides
    @HubBaseUrl
    fun hubBaseUrl(resources: Resources) = resources.getString(R.string.hub_endpoint)

    @Singleton
    @Provides
    @RegistrarBaseUrl
    fun registrarBaseUrl(resources: Resources) = resources.getString(R.string.registrar_endpoint)

    @Singleton
    @Provides
    @LookupBaseUrl
    fun lookupBaseUrl(resources: Resources) = resources.getString(R.string.lookup_endpoint_stx)

    @Singleton
    @Provides
    fun provideConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun provideScalarsConverterFactory(): ScalarsConverterFactory =
        ScalarsConverterFactory.create()

    @Singleton
    @Provides
    fun provideFlowCallAdapterFactory(): FlowCallAdapterFactory =
        FlowCallAdapterFactory.create()

    @Singleton
    @Provides
    fun loggingInterceptor() =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun socketFactory(): DelegatingSocketFactory =
        DelegatingSocketFactory(javax.net.SocketFactory.getDefault())


    @Provides
    @Singleton
    fun httpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        socketFactory: DelegatingSocketFactory,
    ) =
        OkHttpClient.Builder()
            .socketFactory(socketFactory)
            .apply { if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor) }
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun baseRetrofitBuilder(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
        scalarsConverterFactory: ScalarsConverterFactory,
        flowCallAdapterFactory: FlowCallAdapterFactory,
    ): Retrofit.Builder = Retrofit.Builder()
        .client(okHttpClient)
        .addCallAdapterFactory(flowCallAdapterFactory)
        .addConverterFactory(scalarsConverterFactory)
        .addConverterFactory(gsonConverterFactory)

    @Provides
    @Singleton
    fun nonBaseUrlRetrofit(
        builder: Retrofit.Builder
    ): Retrofit = builder.build()

    @Provides
    fun provideGenericService(retrofit: Retrofit): GenericService =
        retrofit.create(GenericService::class.java)

    @Provides
    @Singleton
    @GaiaRetrofit
    fun gaiaRetrofit(
        builder: Retrofit.Builder,
        @GaiaBaseUrl baseUrl: String
    ): Retrofit = builder
        .baseUrl(baseUrl)
        .build()

    @Provides
    fun provideGaiaService(@GaiaRetrofit retrofit: Retrofit): GaiaService =
        retrofit.create(GaiaService::class.java)

    @Provides
    @Singleton
    @HubRetrofit
    fun hubRetrofit(
        builder: Retrofit.Builder,
        @HubBaseUrl baseUrl: String
    ): Retrofit = builder
        .baseUrl(baseUrl)
        .build()

    @Provides
    fun provideHubService(@HubRetrofit retrofit: Retrofit): HubService =
        retrofit.create(HubService::class.java)

    @Provides
    @Singleton
    @RegistrarRetrofit
    fun registrarRetrofit(
        builder: Retrofit.Builder,
        @RegistrarBaseUrl baseUrl: String
    ): Retrofit = builder
        .baseUrl(baseUrl)
        .build()

    @Provides
    @Singleton
    @LookupRetrofit
    fun lookupRetrofit(
        builder: Retrofit.Builder,
        @LookupBaseUrl baseUrl: String
    ): Retrofit = builder
        .baseUrl(baseUrl)
        .build()

    @Provides
    fun provideRegistrarService(@RegistrarRetrofit retrofit: Retrofit): RegistrarService =
        retrofit.create(RegistrarService::class.java)

    @Provides
    fun provideLookupService(@LookupRetrofit retrofit: Retrofit): LookupService =
        retrofit.create(LookupService::class.java)

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class GaiaBaseUrl

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class GaiaRetrofit

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class HubBaseUrl

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class HubRetrofit

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RegistrarBaseUrl

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LookupBaseUrl

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RegistrarRetrofit

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LookupRetrofit

}
