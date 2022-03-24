package com.vmloft.develop.app.template.app

import com.vmloft.develop.app.template.request.repository.*
import com.vmloft.develop.app.template.request.viewmodel.*
import com.vmloft.develop.library.image.display.DisplayViewModel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Create by lzan13 on 2020/6/4 18:02
 */
val viewModelModule = module {
    viewModel { com.vmloft.develop.library.image.display.DisplayViewModel() }
    viewModel { ExploreViewModel(get(), get()) }
    viewModel { FeedbackViewModel(get()) }
    viewModel { UserViewModel(get(), get(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { MatchViewModel(get(), get()) }
    viewModel { TradeViewModel(get()) }
    viewModel { PostViewModel(get(), get(), get()) }
    viewModel { RoomViewModel(get()) }
    viewModel { SignViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { CommonViewModel(get()) }

}

val repositoryModule = module {
//    single { APIRequest.getAPI(APIService::class.java, CConstants.baseHost()) }
    single { CoroutinesDispatcherProvider() }
    single { CommonRepository() }
    single { FollowRepository() }
    single { InfoRepository() }
    single { LikeRepository() }
    single { MainRepository() }
    single { MatchRepository() }
    single { TradeRepository() }
    single { PostRepository() }
    single { RoomRepository() }
    single { SignRepository() }
}

val appModule = listOf(viewModelModule, repositoryModule)