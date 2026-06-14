package com.example.registrocriminal

import android.app.Application

class RegistroCriminalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimenRepository.inicializar(this)
    }
}
