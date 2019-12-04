package edu.uw.tran6388.ninkawalk

import android.app.Service
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*
import edu.uw.tran6388.ninkawalk.ui.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.math.roundToInt
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import edu.uw.tran6388.ninkawalk.ui.Pokemon


class MainActivity : AppCompatActivity(), SensorEventListener {

    var running = false
    var sensorManager:SensorManager? = null
    var totalPoints: String? = "0"
    var previoiusSteps = 0
    //private var query: String? = ""
    private val QUERY_STRING: String = "search query"
    var steps = 0
    var collectionPokemon = mutableMapOf<Int, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        var context = Context.SENSOR_SERVICE
        sensorManager = getSystemService(context) as SensorManager
        navView.setupWithNavController(navController)
        //findViewById<TextView>(R.id.text_display).text = "0"
        //text_display.text = "0"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(QUERY_STRING, totalPoints)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        running = true
        Log.d("onResume", "START")
        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepsSensor == null) {
            Log.d("onResume", "No Step Counter Sensor !")
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        Log.d("onSensorChanged", "Sensor changed!")
        if (running) {
            Log.d("onSensorChanged", "Updating Steps")
            score += steps - previoiusPoints
            previousPoints = steps
            text_display.text = event.values[0].roundToInt().toString() + " STEPS"
            steps = event.values[0].roundToInt()
        }
    }

}