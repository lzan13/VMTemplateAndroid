package com.vmloft.develop.app.template.app

import com.vmloft.develop.app.template.request.repository.*
import com.vmloft.develop.app.template.ui.feedback.FeedbackViewModel
import com.vmloft.develop.library.common.ui.display.DisplayViewModel
import com.vmloft.develop.app.template.ui.main.explore.ExploreViewModel
import com.vmloft.develop.app.template.ui.main.MainViewModel
import com.vmloft.develop.app.template.ui.main.home.MatchViewModel
import com.vmloft.develop.app.template.ui.main.mine.info.InfoViewModel
import com.vmloft.develop.app.template.ui.post.PostViewModel
import com.vmloft.develop.app.template.ui.sign.SignViewModel
import com.vmloft.develop.app.template.ui.user.UserInfoViewModel
import com.vmloft.develop.app.template.ui.room.RoomViewModel
import com.vmloft.develop.app.template.ui.settings.SettingsViewModel
import com.vmloft.develop.library.common.request.FileRepository

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Create by lzan13 on 2020/6/4 18:02
 */
val viewModelModule = module {
    viewModel { DisplayViewModel() }
    viewModel { ExploreViewModel(get(), get()) }
    viewModel { FeedbackViewModel(get(), get()) }
    viewModel { InfoViewModel(get(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { MatchViewModel(get()) }
    viewModel { PostViewModel(get(), get(), get(), get()) }
    viewModel { RoomViewModel(get()) }
    viewModel { SignViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { UserInfoViewModel(get(), get()) }

}

val repositoryModule = module {
//    single { APIRequest.getAPI(APIService::class.java, CConstants.baseHost()) }
    single { CoroutinesDispatcherProvider() }
    single { CommonRepository() }
    single { FileRepository() }
    single { FollowRepository() }
    single { InfoRepository() }
    single { LikeRepository() }
    single { MainRepository() }
    single { MatchRepository() }
    single { PostRepository() }
    single { RoomRepository() }
    single { SignRepository() }
}

val appModule = listOf(viewModelModule, repositoryModule)