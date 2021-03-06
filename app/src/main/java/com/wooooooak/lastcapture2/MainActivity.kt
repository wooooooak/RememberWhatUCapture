package com.wooooooak.lastcapture2

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.wooooooak.lastcapture2.data.AppDataBase
import com.wooooooak.lastcapture2.data.repository.AlbumRepositoryImpl
import com.wooooooak.lastcapture2.data.source.local.AlbumLocalDataSource
import com.wooooooak.lastcapture2.ui.AlbumListViewModelFactory
import com.wooooooak.lastcapture2.ui.LastCaptureTheme
import com.wooooooak.lastcapture2.ui.component.Screen
import com.wooooooak.lastcapture2.ui.component.album_list.AlbumListScreen
import com.wooooooak.lastcapture2.ui.component.album_list.AlbumListViewModel
import com.wooooooak.lastcapture2.ui.component.picture_list.PictureListScreen
import kotlinx.coroutines.Dispatchers
import wooooooak.com.library.CoroutinesPermissionManager
import wooooooak.com.library.PermissionResult

class MainActivity : AppCompatActivity() {
    private val bottomNavigationItem = listOf(
        Screen.PictureList,
        Screen.AlbumList,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission(
            onGranted = { setContent() },
            onDenied = { finish() }
        )
    }

    private fun requestPermission(onGranted: () -> Unit, onDenied: () -> Unit) {
        lifecycleScope.launchWhenCreated {
            val result = CoroutinesPermissionManager.requestPermission(this@MainActivity) {
                permissionList = listOf(READ_EXTERNAL_STORAGE)
                Rationale {
                    title = "저장공간 권한 필요"
                    message = "디바이스의 이미지에 접근하려면 저장 공간 권한이 필요합니다."
                    confirmText = "허용하기"
                }
            }
            when (result) {
                PermissionResult.Granted -> onGranted()
                is PermissionResult.Denied -> onDenied()
            }
        }
    }

    private fun setContent() {
        setContent {
            val navController = rememberNavController()

            val albumListViewModel by viewModels<AlbumListViewModel> {
                AlbumListViewModelFactory(
                    AlbumRepositoryImpl(
                        AlbumLocalDataSource(
                            contentResolver,
                            AppDataBase.getInstance(this)?.albumDao()
                        ),
                        Dispatchers.IO
                    )
                )
            }

            LastCaptureTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigation {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
                            bottomNavigationItem.forEach { screen ->
                                LcBottomNavigationItem(navController = navController, screen = screen, currentRoute)
                            }
                        }
                    },
                ) { padding ->
                    NavHost(navController, startDestination = Screen.PictureList.route) {
                        composable(Screen.PictureList.route) {
                            Surface(modifier = Modifier.padding(bottom = padding.bottom)) {
                                PictureListScreen(albumListViewModel) { pictureUri ->
                                    startPictureDetail(pictureUri)
                                }
                            }
                        }
                        composable(Screen.AlbumList.route) {
                            Surface(modifier = Modifier.padding(bottom = padding.bottom)) {
                                AlbumListScreen(albumListViewModel)
                            }
                        }
//                        composable(
//                            "${Screen.PictureDetail.route}/{uri}",
//                            arguments = listOf(navArgument("uri") {
//                                type = NavType.StringType
//                            })
//                        ) { navBackStackEntry ->
//                            PictureDetailScreen(navBackStackEntry.arguments?.getString("uri"))
//                        }
                    }
                }
            }
        }
    }

    private fun startPictureDetail(uri: String) {
        val intent = Intent(this, PictureDetailActivity::class.java).apply {
            putExtra("uri", uri)
        }
        startActivity(intent)
    }
}


@Composable
private fun LcBottomNavigationItem(
    navController: NavController,
    screen: Screen,
    currentRoute: String?,
) {
    val isSelected = screen.route == currentRoute
    BottomNavigationItem(
        icon = { BottomNavImage(screen, isSelected) },
        label = { Text(text = screen.name) },
        selected = isSelected,
        onClick = {
            if (currentRoute != screen.route) {
                navController.popBackStack(navController.graph.startDestination, false)
                navController.navigate(screen.route)
            }
        }
    )
}

@Composable
private fun BottomNavImage(screen: Screen, isSelected: Boolean) {
    when (screen.route) {
        Screen.PictureList.route -> if (isSelected) Image(Icons.Filled.PhotoAlbum) else Image(Icons.Outlined.PhotoAlbum)
        Screen.AlbumList.route -> if (isSelected) Image(Icons.Filled.Image) else Image(Icons.Outlined.Image)
    }
}
