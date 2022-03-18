package com.developers.healtywise.di

import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.data.netWork.account.AccountService
import com.developers.healtywise.data.netWork.repository.AccountRepositoryImpl
import com.developers.healtywise.di.qualifiers.IOThread
import com.developers.healtywise.di.qualifiers.MainThread
import com.developers.healtywise.domin.repository.AccountRepository
import com.developers.healtywise.R
import com.developers.healtywise.data.netWork.repository.MainRepositoryImpl
import com.developers.healtywise.domin.repository.MainRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModel {


    @Singleton
    @Provides
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ) = context

    // TODO: 11/8/2021  For implementation MainDispatcher

    @MainThread
    @Singleton
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main


    // TODO: 11/8/2021  For implementation IODispatcher

    @IOThread
    @Singleton
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO


    // TODO: 11/8/2021  For implementation Glide
    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)

    )

    @Provides
    @Singleton
    fun dataStoreManager(@ApplicationContext appContext: Context): DataStoreManager =
        DataStoreManager(appContext)

    @Provides
    @Singleton
    fun provideAccountRepository(account: AccountService, auth: FirebaseAuth): AccountRepository {
        return AccountRepositoryImpl(account,auth)
    }

    @Provides
    @Singleton
    fun provideMainRepository(account: AccountService, auth: FirebaseAuth): MainRepository {
        return MainRepositoryImpl(account,auth)
    }

   @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }


    @SuppressLint("NewApi")
    @Singleton
    @Provides
    fun provideSimpleDateFormat(): SimpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa", Locale.US)


    @Singleton
    @Provides
    fun provideDate(): Date = Date()




}