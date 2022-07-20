package com.vmloft.develop.app.template.app

import com.vmloft.develop.app.template.request.viewmodel.SignViewModel
import com.vmloft.develop.library.data.repository.*
import com.vmloft.develop.library.data.viewmodel.*
import com.vmloft.develop.library.image.display.DisplayViewModel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Create by lzan13 on 2020/6/4 18:02
 */
val viewModelModule = module {
    viewModel { AppletViewModel(get(), get()) }
    viewModel { CommonViewModel(get()) }
    viewModel { DisplayViewModel() }
    viewModel { ExploreViewModel(get(), get()) }
    viewModel { FeedbackViewModel(get()) }
    viewModel { GiftViewModel(get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { MatchViewModel(get(), get()) }
    viewModel { PostViewModel(get(), get(), get()) }
    viewModel { RoomViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { SignViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { TradeViewModel(get()) }
    viewModel { UserViewModel(get(), get(), get(), get(), get()) }

}

val repositoryModule = module {
//    single { APIRequest.getAPI(APIService::class.java, CConstants.baseHost()) }
    single { CoroutinesDispatcherProvider() }
    single { AppletRepository() }
    single { BlacklistRepository() }
    single { CommonRepository() }
    single { GiftRepository() }
    single { InfoRepository() }
    single { LikeRepository() }
    single { MainRepository() }
    single { MatchRepository() }
    single { PostRepository() }
    single { RelationRepository() }
    single { RoomRepository() }
    single { SignRepository() }
    single { TradeRepository() }
}

val appModule = listOf(viewModelModule, repositoryModule)