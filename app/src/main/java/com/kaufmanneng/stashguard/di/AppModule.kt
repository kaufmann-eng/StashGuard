package com.kaufmanneng.stashguard.di

import com.kaufmanneng.stashguard.data.datasource.ProductCategoryDataSource
import com.kaufmanneng.stashguard.data.datasource.ProductLocalDataSource
import com.kaufmanneng.stashguard.data.datasource.SettingsLocalDataSource
import com.kaufmanneng.stashguard.data.repository.ProductCategoryRepositoryImpl
import com.kaufmanneng.stashguard.data.repository.ProductRepositoryImpl
import com.kaufmanneng.stashguard.data.repository.SettingsRepositoryImpl
import com.kaufmanneng.stashguard.domain.repository.ProductCategoryRepository
import com.kaufmanneng.stashguard.domain.repository.ProductRepository
import com.kaufmanneng.stashguard.domain.repository.SettingsRepository
import com.kaufmanneng.stashguard.framework.local.ProductCategoryLocalDataSourceImpl
import com.kaufmanneng.stashguard.framework.local.ProductLocalDataSourceImpl
import com.kaufmanneng.stashguard.framework.local.SettingsLocalDataSourceImpl
import com.kaufmanneng.stashguard.framework.local.database.AppDataBase
import com.kaufmanneng.stashguard.framework.local.datastore.AppSettingsDataStore
import com.kaufmanneng.stashguard.presentation.main.MainViewModel
import com.kaufmanneng.stashguard.presentation.productcategorymanagement.ProductCategoryManagementViewModel
import com.kaufmanneng.stashguard.presentation.productform.ProductFormViewModel
import com.kaufmanneng.stashguard.presentation.productlist.ProductListViewModel
import com.kaufmanneng.stashguard.presentation.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database, DAOs, DataStores
    single { AppDataBase.getInstance(androidContext()) }
    factory { get<AppDataBase>().productDao() }
    factory { get<AppDataBase>().productCategoryDao() }
    single { AppSettingsDataStore(androidContext()) }

    // DataSources
    single<ProductLocalDataSource> { ProductLocalDataSourceImpl(get(), get(), androidContext()) }
    single<ProductCategoryDataSource> { ProductCategoryLocalDataSourceImpl(get()) }
    single<SettingsLocalDataSource> { SettingsLocalDataSourceImpl(get()) }

    // Repositories
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    single<ProductCategoryRepository> { ProductCategoryRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    // ViewModels
    viewModel<ProductListViewModel> { ProductListViewModel(get(), get()) }
    viewModel<ProductFormViewModel> { ProductFormViewModel(get(), get(), get()) }
    viewModel<ProductCategoryManagementViewModel> { ProductCategoryManagementViewModel(get()) }
    viewModel<MainViewModel> { MainViewModel(get()) }
    viewModel<SettingsViewModel> { SettingsViewModel(get()) }

}