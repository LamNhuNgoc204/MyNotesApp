package com.example.my_note_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appbroken_rice.ui.theme.MyNotesApp
import com.example.my_note_app.detail.DetailModel
import com.example.my_note_app.home.HomeModel
import com.example.my_note_app.login.LoginModel
import com.example.my_note_app.router.Navigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val loginModel = viewModel(modelClass = LoginModel::class.java)
            val homeModel = viewModel(modelClass = HomeModel::class.java)
            val detailModel = viewModel(modelClass = DetailModel::class.java)
            MyNotesApp {
                Surface(modifier = Modifier, color = MaterialTheme.colorScheme.background) {
                    Navigation(loginModel = loginModel, homeModel = homeModel, detailModel = detailModel)
                }
            }
        }

    }
}