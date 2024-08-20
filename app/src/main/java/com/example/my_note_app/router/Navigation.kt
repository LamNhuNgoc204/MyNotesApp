package com.example.my_note_app.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.my_note_app.detail.DetailModel
import com.example.my_note_app.detail.DetailScreen
import com.example.my_note_app.home.HomeModel
import com.example.my_note_app.home.HomeScreen
import com.example.my_note_app.login.LoginModel
import com.example.my_note_app.login.LoginScreen
import com.example.my_note_app.login.SignUpScreen

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginModel: LoginModel,
    detailModel: DetailModel,
    homeModel: HomeModel
) {
    NavHost(navController = navController, startDestination = NestedRoutes.Main.name){
        authGraph(navController, loginModel)
        homeGraph(navController, detailModel, homeModel)
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginModel: LoginModel
){
    navigation(
        startDestination = LoginRoutes.SignIn.name,
        route = NestedRoutes.Login.name
    ){
        composable(route = LoginRoutes.SignIn.name){
            LoginScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name){
                    launchSingleTop = true
                    popUpTo(route = LoginRoutes.SignIn.name){
                        inclusive = true
                    }
                }
            },
                loginModel = loginModel
            ) {
                navController.navigate(LoginRoutes.SignUp.name){
                    launchSingleTop = true
                    popUpTo(route = LoginRoutes.SignIn.name){
                        inclusive = true
                    }
                }
            }
        }

        composable(route = LoginRoutes.SignUp.name){
            SignUpScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name){
                    popUpTo(route = LoginRoutes.SignUp.name){
                        inclusive = true
                    }
                }
            },
                loginModel = loginModel
            ) {
                navController.navigate(LoginRoutes.SignIn.name)
            }
        }
    }

}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    detailModel: DetailModel,
    homeModel: HomeModel
){
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name
    ){
        composable(HomeRoutes.Home.name){
            HomeScreen(
                homeModel = homeModel,
                onNoteClick = { noteId ->
                    navController.navigate(HomeRoutes.Detail.name + "?id=$noteId"){
                        launchSingleTop = true
                    }
                },
                navToDetailPage = {
                    navController.navigate(HomeRoutes.Detail.name)
                }
            ) {
                navController.navigate(NestedRoutes.Login.name){
                    launchSingleTop = true
                    popUpTo(0){
                        inclusive = true
                    }
                }
            }
        }

        composable(route = HomeRoutes.Detail.name + "?id={id}",
            arguments = listOf(navArgument("id"){
                type = NavType.StringType
                defaultValue = ""
            })
        ){ navBackStackEntry ->
            DetailScreen(
                detailModel = detailModel,
                noteId = navBackStackEntry.arguments?.getString("id") ?: ""
            ) {
                navController.navigateUp()
            }
        }
    }
}
