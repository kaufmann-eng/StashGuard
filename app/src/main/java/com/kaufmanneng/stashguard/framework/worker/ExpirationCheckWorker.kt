package com.kaufmanneng.stashguard.framework.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kaufmanneng.stashguard.domain.repository.ProductRepository
import com.kaufmanneng.stashguard.framework.notification.ProductNotifier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExpirationCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val productRepository: ProductRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            val expiringProducts = productRepository.getProductsExpiringSoon(daysInAdvance = 7)
            if (expiringProducts.isNotEmpty()) {
                ProductNotifier.show(applicationContext, expiringProducts)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}