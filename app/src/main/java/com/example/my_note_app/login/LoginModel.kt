package com.example.my_note_app.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_note_app.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginModel(
    private val repository: AuthRepository = AuthRepository()
):ViewModel() {
    val currentUser = repository.currentUser

    val hasUser:Boolean get() = repository.hasUser()

    var LoginState by mutableStateOf(LoginState())
    private set

    fun onUserNameChange(userName: String){
        LoginState = LoginState.copy(userName = userName)
    }
    fun onPasswordChange(password: String){
        LoginState = LoginState.copy(password = password)
    }
    fun onUserNameChangeSignUp(userName: String){
        LoginState = LoginState.copy(userNameSignup = userName)
    }
    fun onPasswordChangeSignUp(password: String){
        LoginState = LoginState.copy(passwordSignup = password)
    }
    fun onConfirmPasswordSignup(password: String){
        LoginState = LoginState.copy(confirmPasswordSignup = password)
    }

    private fun validateLogin() = LoginState.userName.isNotBlank() && LoginState.password.isNotBlank()
    private fun validateSignup() = LoginState.userNameSignup.isNotBlank() && LoginState.passwordSignup.isNotBlank() && LoginState.confirmPasswordSignup.isNotBlank()

    fun createUser(context: Context) = viewModelScope.launch {
        try {
            if (!validateSignup()){
                throw IllegalArgumentException("Email and password is required!")
            }

            LoginState = LoginState.copy(isLoading = true)
            if(LoginState.passwordSignup != LoginState.confirmPasswordSignup){
                throw IllegalArgumentException("Password is not match!")
            }

            LoginState = LoginState.copy(signupError = null)
            repository.createUser(LoginState.userNameSignup, LoginState.passwordSignup){it->
                if(it){
                    Toast.makeText(context,"Login success", Toast.LENGTH_SHORT).show()
                    LoginState = LoginState.copy(isSuccess = true)
                }else{
                    Toast.makeText(context,"Login failed", Toast.LENGTH_SHORT).show()
                    LoginState = LoginState.copy(isSuccess = false)

                }
            }

        }catch (e: Exception){
            LoginState = LoginState.copy(signupError = e.localizedMessage)
            e.printStackTrace()
        }finally {
            LoginState = LoginState.copy(isLoading = false)
        }
    }

    fun loginUser(context: Context) = viewModelScope.launch {
        try {
            if (!validateLogin()){
                throw IllegalArgumentException("Email and password is required!")
            }

            LoginState = LoginState.copy(isLoading = true)

            LoginState = LoginState.copy(loginError = null)
            repository.login(LoginState.userName, LoginState.password){it->
                if(it){
                    Toast.makeText(context,"Login success", Toast.LENGTH_SHORT).show()
                    LoginState = LoginState.copy(isSuccess = true)
                }else{
                    Toast.makeText(context,"Login failed", Toast.LENGTH_SHORT).show()
                    LoginState = LoginState.copy(isSuccess = false)

                }
            }

        }catch (e: Exception){
            LoginState = LoginState.copy(loginError = e.localizedMessage)
            e.printStackTrace()
        }finally {
            LoginState = LoginState.copy(isLoading = false)
        }
    }
}

data class LoginState(
    val userName: String = "",
    val password: String = "",
    val userNameSignup: String = "",
    val passwordSignup: String = "",
    val confirmPasswordSignup: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val signupError: String ?= null,
    val loginError: String ?= null
)