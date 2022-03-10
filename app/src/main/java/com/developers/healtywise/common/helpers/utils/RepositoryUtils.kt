package com.developers.healtywise.common.helpers.utils

import com.developers.healtywise.common.helpers.Resource


inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch(e: Exception) {
        Resource.Error(e.message ?: "An unknown error occurred")
    }
}

//inline fun <T> safeNavigate(action: () -> T): T {
//    return try {
//        action()
//    } catch(e: Exception) {
//        ""
//    }
//}










