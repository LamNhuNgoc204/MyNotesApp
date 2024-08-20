package com.example.my_note_app.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appbroken_rice.ui.theme.MyNotesApp

@Composable
fun SignUpScreen(
    loginModel: LoginModel ?= null,
    onNavToHomePage:() -> Unit,
    onNavToLoginPage:() -> Unit,
) {
    val loginState = loginModel?.LoginState
    val isErr = loginState?.signupError != null
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.h3,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colors.primary
        )

        if(isErr){
            Text(text = loginState?.signupError.toString() ?: "Unknow error", color = Color.Red)
        }

        OutlinedTextField(value = loginState?.userNameSignup ?: "",
            onValueChange = {loginModel?.onUserNameChangeSignUp(it)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            },
            label = {
                Text(text = "Email")
            },
            isError = isErr
        )

        OutlinedTextField(value = loginState?.passwordSignup ?: "",
            onValueChange = {loginModel?.onPasswordChangeSignUp(it)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
            },
            label = {
                Text(text = "Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isErr
        )

        OutlinedTextField(value = loginState?.confirmPasswordSignup ?: "",
            onValueChange = {loginModel?.onConfirmPasswordSignup(it)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
            },
            label = {
                Text(text = "Confirm password")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isErr
        )

        Button(onClick = { loginModel?.createUser(context) }) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.size(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "You have a account?")
            Spacer(modifier = Modifier.size(6.dp))
            TextButton(onClick = { onNavToLoginPage.invoke() }) {
                Text(text = "Sign In")
            }
        }

        if(loginState?.isLoading == true){
            CircularProgressIndicator()
        }

        LaunchedEffect(key1 = loginModel?.hasUser) {
            if(loginModel?.hasUser == true){
                onNavToHomePage.invoke()
            }
        }

    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewLogin() {
    MyNotesApp {
        SignUpScreen(onNavToHomePage = { /*TODO*/ }) {

        }
    }
}