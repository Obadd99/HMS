package com.developers.healtywise.di

import android.annotation.SuppressLint
import android.content.Context

import com.google.android.gms.location.FusedLocationProviderClient

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import java.text.SimpleDateFormat
import java.util.*

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelHilt {

    @SuppressLint("NewApi")
    @ViewModelScoped
    @Provides
    fun provideSimpleDateFormat(): SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd h:mm", Locale.US)


    @ViewModelScoped
    @Provides
    fun provideDate(): Date = Date()




    @Provides
    @ViewModelScoped
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context,
    ) = FusedLocationProviderClient(app)


}