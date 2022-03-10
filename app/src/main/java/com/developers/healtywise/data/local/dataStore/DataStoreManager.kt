package com.developers.healtywise.data.local.dataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.common.helpers.utils.Constants.HOLDER_ICON
import com.developers.healtywise.common.helpers.utils.Constants.USERS_INFO_FILE
import com.developers.healtywise.common.helpers.utils.Constants.USER_BIRTH_DATE
import com.developers.healtywise.common.helpers.utils.Constants.USER_DOCTOR
import com.developers.healtywise.common.helpers.utils.Constants.USER_EMAIL1
import com.developers.healtywise.common.helpers.utils.Constants.USER_FIRST_NAME
import com.developers.healtywise.common.helpers.utils.Constants.USER_ID
import com.developers.healtywise.common.helpers.utils.Constants.USER_IMAGE_PROFILE
import com.developers.healtywise.common.helpers.utils.Constants.USER_LAST_NAME
import com.developers.healtywise.common.helpers.utils.Constants.USER_MALE
import com.developers.healtywise.common.helpers.utils.Constants.USER_MOBILE
import com.developers.healtywise.common.helpers.utils.Constants.USER_TOKEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


private val Context.dataStore by preferencesDataStore(USERS_INFO_FILE)


class DataStoreManager constructor(appContext: Context) {

    private val dataStoreManger = appContext.dataStore

//       private val scope = CoroutineScope(Job() + Dispatchers.Main)
//    private val _userInfoFlow = MutableLiveData<String>()
//    private val _currentMethod = MutableLiveData<String>()

    // For public variables, prefer use LiveData just to read values.
//    val userInfoFlow: LiveData<String> get() = _userInfoFlow
//    val currentMethod: LiveData<String> get() = _currentMethod


    // generic values

    suspend fun<T> putValue(key:Preferences.Key<T>,value:T){
        dataStoreManger.edit { preferences ->
            preferences[key] = value
        }
    }

    fun<T> getValue(key:Preferences.Key<T>): Flow<T?> = dataStoreManger.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[key]
    }

    suspend fun setToken(token: String) {
        dataStoreManger.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }


    suspend fun saveUserProfile(user: User) = dataStoreManger.edit { preferences ->
        preferences[USER_ID] = user.userId
        preferences[USER_FIRST_NAME] = user.firstName
        preferences[USER_LAST_NAME] = user.lastName
        preferences[USER_EMAIL1] = user.email
        preferences[USER_MOBILE] = user.mobile
        preferences[USER_BIRTH_DATE] = user.birthDate
        preferences[USER_IMAGE_PROFILE] = user.imageProfile
        preferences[USER_MALE] = user.male
        preferences[USER_DOCTOR] = user.doctor
    }

    suspend fun logOut()=dataStoreManger.edit {
            it.clear()
        }



    fun getUserTokenORId(): Flow<String> = dataStoreManger.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[USER_TOKEN] ?: ""
    }


    fun getUserProfile(): Flow<User> = dataStoreManger.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        User(
            userId = preferences[USER_ID]?:"",
            firstName = preferences[USER_FIRST_NAME] ?: "",
            lastName = preferences[USER_LAST_NAME] ?: "",
            email = preferences[USER_EMAIL1] ?: "",
            mobile = preferences[USER_MOBILE] ?: "",
            imageProfile = preferences[USER_IMAGE_PROFILE] ?: HOLDER_ICON,
            birthDate = preferences[USER_BIRTH_DATE] ?:"",
            doctor =  preferences[USER_DOCTOR] ?:false,
            male =  preferences[USER_MALE] ?:true
        )

    }


}