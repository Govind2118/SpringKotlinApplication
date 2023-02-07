package controllers

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton

class DemoApplicationModule : AbstractModule() {

    override fun configure() {
        requestStaticInjection(ConversionController::class.java)
    }

    @Provides
    @Singleton
    fun conversionProvider(): ConversionController = ConversionController()
}