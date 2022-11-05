package com.musalatask.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.musalatask.weatherapp.common.Constants
import com.musalatask.weatherapp.data.local.CityWeatherLocalDataSource
import com.musalatask.weatherapp.data.local.CoordinatesLocalDataSource
import com.musalatask.weatherapp.data.remote.CityWeatherRemoteDataSource
import com.musalatask.weatherapp.data.remote.CoordinatesRemoteDataSource
import com.musalatask.weatherapp.data.repositoryImpl.CityWeatherRepositoryImpl
import com.musalatask.weatherapp.data.repositoryImpl.GeocodingRepositoryImpl
import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import com.musalatask.weatherapp.domain.repository.GeocodingRepository
import com.musalatask.weatherapp.domain.use_case.GetACityWeather
import com.musalatask.weatherapp.domain.use_case.GetMyCurrentCityWeather
import com.musalatask.weatherapp.framework.retrofit.CityWeatherApi
import com.musalatask.weatherapp.framework.retrofit.GeocodingApi
import com.musalatask.weatherapp.framework.retrofit.SecurityInterceptor
import com.musalatask.weatherapp.framework.retrofit.dataSourceImpl.CityWeatherRemoteDataSourceImpl
import com.musalatask.weatherapp.framework.retrofit.dataSourceImpl.CoordinatesRemoteDataSourceImpl
import com.musalatask.weatherapp.framework.room.dataSourceImpl.CityWeatherLocalDataSourceImpl
import com.musalatask.weatherapp.framework.room.dataSourceImpl.CoordinatesLocalDataSourceImpl
import com.musalatask.weatherapp.framework.room.db.AppDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDB(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            Constants.DB_NAME
        ).build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(SecurityInterceptor())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideCityWeatherApi(retrofit: Retrofit): CityWeatherApi =
        retrofit.create(CityWeatherApi::class.java)

    @Provides
    @Singleton
    fun provideGeocodingApi(retrofit: Retrofit): GeocodingApi =
        retrofit.create(GeocodingApi::class.java)

    @Provides
    @Singleton
    fun provideCoordinatesLocalDataSource(db: AppDataBase): CoordinatesLocalDataSource =
        CoordinatesLocalDataSourceImpl(db)

    @Provides
    @Singleton
    fun provideCoordinatesRemoteDataSource(api: GeocodingApi): CoordinatesRemoteDataSource =
        CoordinatesRemoteDataSourceImpl(api, Dispatchers.IO)

    @Provides
    @Singleton
    fun provideGeocodingRepository(
        coordinatesLocalDataSource: CoordinatesLocalDataSource,
        coordinatesRemoteDataSource: CoordinatesRemoteDataSource
    ): GeocodingRepository =
        GeocodingRepositoryImpl(
            localDataSource = coordinatesLocalDataSource,
            remoteDataSource = coordinatesRemoteDataSource
        )

    @Provides
    @Singleton
    fun provideCityWeatherLocalDataSource(db: AppDataBase): CityWeatherLocalDataSource =
        CityWeatherLocalDataSourceImpl(db)

    @Provides
    @Singleton
    fun provideCityWeatherRemoteDataSource(api: CityWeatherApi): CityWeatherRemoteDataSource =
        CityWeatherRemoteDataSourceImpl(api, Dispatchers.IO)

    @Provides
    @Singleton
    fun provideCityWeatherRepository(
        cityWeatherLocalDataSource: CityWeatherLocalDataSource,
        cityWeatherRemoteDataSource: CityWeatherRemoteDataSource,
        geocodingRepository: GeocodingRepository
    ): CityWeatherRepository =
        CityWeatherRepositoryImpl(
            localDataSource = cityWeatherLocalDataSource,
            remoteDataSource = cityWeatherRemoteDataSource,
            geocodingRepository = geocodingRepository
        )

    @Provides
    @Singleton
    fun provideGetACityWeatherUseCase(cityWeatherRepository: CityWeatherRepository): GetACityWeather =
        GetACityWeather(cityWeatherRepository)

    @Provides
    @Singleton
    fun provideGetMyCurrentCityWeatherUseCase(cityWeatherRepository: CityWeatherRepository): GetMyCurrentCityWeather =
        GetMyCurrentCityWeather(cityWeatherRepository)
}