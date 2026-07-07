package com.petspa.app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.petspa.app.data.BookingDataStore
import com.petspa.app.data.TokenDataStore
import com.petspa.app.data.local.database.DatabaseProvider
import com.petspa.app.network.RetrofitProvider
import com.petspa.app.repository.PetSpaRepository

class AppViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {

    private val repo: PetSpaRepository by lazy {

        val db =
            DatabaseProvider.getDatabase(app)

        val api = RetrofitProvider.createApi(app)
        PetSpaRepository(
            api = api,
            db = db,
            tokenStore = TokenDataStore(app),
            bookingStore = BookingDataStore(app)
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val savedStateHandle = extras.createSavedStateHandle()

        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(repo) as T

            modelClass.isAssignableFrom(CustomerViewModel::class.java) ->
                CustomerViewModel(repo, savedStateHandle) as T

            modelClass.isAssignableFrom(
                StaffViewModel::class.java
            ) ->
                StaffViewModel(repo) as T

            modelClass.isAssignableFrom(
                OwnerViewModel::class.java
            ) ->
                OwnerViewModel(repo) as T

            else ->
                throw IllegalArgumentException(
                    "Unknown ViewModel: ${modelClass.name}"
                )
        }
    }
}