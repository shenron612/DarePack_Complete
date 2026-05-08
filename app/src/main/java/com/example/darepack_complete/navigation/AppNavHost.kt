package com.example.darepack_complete.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.darepack_complete.darepack.*

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.SPLASH) {
            SplashScreen(onNext = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.LOGIN) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }

        composable(Routes.HOME) {
            HomeScreen(
                onDareClick  = { dareId -> navController.navigate(Routes.dareDetail(dareId)) },
                onNavGroups  = { navController.navigate(Routes.GROUPS) },
                onNavBucket  = { navController.navigate(Routes.BUCKET_LIST) },
                onNavProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.GROUPS) {
            GroupsScreen(
                onCreateGroup = { navController.navigate(Routes.CREATE_GROUP) },
                onInviteFriends = { groupId -> navController.navigate(Routes.inviteFriends(groupId)) },
                onNavHome     = { navController.navigate(Routes.HOME) },
                onNavBucket   = { navController.navigate(Routes.BUCKET_LIST) },
                onNavProfile  = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.BUCKET_LIST) {
            BucketListScreen(
                onSendDare   = { itemId, groupId -> navController.navigate(Routes.sendDare(itemId, groupId)) },
                onNavHome    = { navController.navigate(Routes.HOME) },
                onNavGroups  = { navController.navigate(Routes.GROUPS) },
                onNavProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onMemories  = { navController.navigate(Routes.MEMORIES) },
                onSignOut   = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavHome   = { navController.navigate(Routes.HOME) },
                onNavGroups = { navController.navigate(Routes.GROUPS) },
                onNavBucket = { navController.navigate(Routes.BUCKET_LIST) }
            )
        }

        composable(Routes.CREATE_GROUP) {
            CreateGroupScreen(
                onBack = { navController.popBackStack() },
                onGroupCreated = { groupId -> 
                    navController.navigate(Routes.inviteFriends(groupId)) {
                        popUpTo(Routes.CREATE_GROUP) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Routes.INVITE_FRIENDS,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { back ->
            InviteFriendsScreen(
                groupId = back.arguments?.getString("groupId") ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.SEND_DARE,
            arguments = listOf(
                navArgument("itemId")  { type = NavType.StringType },
                navArgument("groupId") { type = NavType.StringType }
            )
        ) { back ->
            SendDareScreen(
                itemId    = back.arguments?.getString("itemId")  ?: "",
                groupId   = back.arguments?.getString("groupId") ?: "",
                onDareSent = { navController.popBackStack() }
            )
        }

        composable(
            Routes.DARE_DETAIL,
            arguments = listOf(navArgument("dareId") { type = NavType.StringType })
        ) { back ->
            DareDetailScreen(
                dareId = back.arguments?.getString("dareId") ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.MEMORIES) {
            MemoriesScreen(onBack = { navController.popBackStack() })
        }
    }
}
