package com.developers.healtywise.presentation.general.setup.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.healtywise.common.helpers.Event
import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val  _userState= Channel<Boolean>()
    val userState=_userState.receiveAsFlow()

    private val _startSplashStatus =
        Channel<Event<Resource<Int>>>()
    val startSplashStatus = _startSplashStatus.receiveAsFlow()

    fun startSplashScreenWithBlueBackground(){
        viewModelScope.launch {
            _startSplashStatus.send(Event(Resource.Loading()))
            delay(1000)
            _startSplashStatus.send(Event(Resource.Success(0)))
        }
    }
    fun startSplashScreenWithLogo(){
        viewModelScope.launch {
            _startSplashStatus.send(Event(Resource.Loading()))
            delay(2000)
            _startSplashStatus.send(Event(Resource.Success(1)))
        }
    }

    fun checkUserState() {
        viewModelScope.launch {
            delay(2000)
            dataStoreManager.getUserProfile().collect{
                if (it.userId.isEmpty()){
                    _userState.send(false)
                }else{
                    _userState.send(true)
                }
            }

        }
    }

}