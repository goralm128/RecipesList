package com.exercise.recipes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        RecipeComponentsController.prepare()
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    as? NavHostFragment
            if (navHostFragment == null) {
                val newNavHostFragment = NavHostFragment.create(R.navigation.nav_graph)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, newNavHostFragment)
                    .setPrimaryNavigationFragment(newNavHostFragment)
                    .commit()
            }
        }
    }
}



