package com.example.deliveryapp.di

import android.content.Context
import com.example.deliveryapp.data.local.DataStoreManager
import com.example.deliveryapp.data.remote.ApiClient
import com.example.deliveryapp.data.remote.api.AuthApi
import com.example.deliveryapp.data.remote.api.OrderApi
import com.example.deliveryapp.data.remote.api.ProductApi
import com.example.deliveryapp.data.remote.interceptor.AuthInterceptor
import com.example.deliveryapp.data.repository.AuthRepository
import com.example.deliveryapp.data.repository.OrderRepository
import com.example.deliveryapp.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(dataStore: DataStoreManager): Interceptor = AuthInterceptor(dataStore)

    @Provides
    @Singleton
    fun provideRetrofit(interceptor: Interceptor): Retrofit = ApiClient.create(interceptor)

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi = retrofit.create(ProductApi::class.java)

    @Provides
    @Singleton
    fun provideOrderApi(retrofit: Retrofit): OrderApi = retrofit.create(OrderApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager = DataStoreManager(context)

    @Provides
    @Singleton
    fun provideProductRepository(api: ProductApi): ProductRepository = ProductRepository(api)

    @Provides
    @Singleton
    fun provideOrderRepository(api: OrderApi): OrderRepository = OrderRepository(api)

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi, dataStore: DataStoreManager): AuthRepository = AuthRepository(api, dataStore)


}