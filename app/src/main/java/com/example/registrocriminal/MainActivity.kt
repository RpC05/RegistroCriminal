package com.example.registrocriminal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Buscamos si ya hay un fragmento cargado en el contenedor
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        // Si no hay ninguno (es la primera vez que se abre la app), creamos y añadimos el CrimenFragment
        if (currentFragment == null) {
            val fragment = CrimenFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
}