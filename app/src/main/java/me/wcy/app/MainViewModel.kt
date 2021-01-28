package me.wcy.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by wcy on 2021/1/25.
 */
class MainViewModel : ViewModel() {

    val mainData = MutableLiveData<String>("s")
}