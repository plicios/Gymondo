package pl.piotrgorny.gymondo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import pl.piotrgorny.gymondo.R


class MainActivity : AppCompatActivity(), AppBarConfiguration.OnNavigateUpListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val host : NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_activity_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController

        setupActionBarWithNavController(
            navController,
            AppBarConfiguration
                .Builder()
                .setFallbackOnNavigateUpListener(this)
                .build()
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_activity_nav_host_fragment)
        if(navController.navigateUp())
            return true
        finish()
        return true
    }
}