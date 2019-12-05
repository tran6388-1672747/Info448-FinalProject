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

import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Handler
import android.os.ResultReceiver

import androidx.lifecycle.ViewModelProviders
import edu.uw.tran6388.ninkawalk.ui.notifications.NotificationsViewModel
import android.R.id.edit
import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.gson.Gson


class MainActivity : AppCompatActivity(), SensorEventListener {

    var running = false
    var sensorManager:SensorManager? = null
    var totalPoints: String? = "0"
    private val QUERY_STRING: String = "search query"
    var steps = 0
    var previousSteps = 0
    var previousSteps2 = 0

    lateinit var notificationsViewModel: NotificationsViewModel

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

        navView.setupWithNavController(navController)
        //findViewById<TextView>(R.id.text_display).text = "0"
        //text_display.text = "0"
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
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
            previousSteps = event.values[0].roundToInt()
            steps += previousSteps - previousSteps2
            previousSteps2 = previousSteps
            text_display.text = steps.toString()
        }
    }

    override fun onStart() {
        super.onStart()

        val mPrefs = getPreferences(Context.MODE_PRIVATE)

        val gson = Gson()
        val json = mPrefs.getString("collection_list", "")
        val notificationsViewModel2 = gson.fromJson<NotificationsViewModel>(json, NotificationsViewModel::class.java!!)

        if (notificationsViewModel2 == null) {
            Log.v("notificationView", "null")
        } else {
            notificationsViewModel = notificationsViewModel2
        }
    }

    override fun onStop() {
        super.onStop()

        val mPrefs = getPreferences(Context.MODE_PRIVATE)

        val prefsEditor = mPrefs.edit();
        val gson = Gson()
        val json = gson.toJson(notificationsViewModel)
        prefsEditor.putString("collection_list", json);
        prefsEditor.commit()
    }
}