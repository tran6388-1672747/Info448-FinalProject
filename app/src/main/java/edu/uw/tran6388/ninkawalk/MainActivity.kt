// Joon Chang, William Fu, Jimmy Tran
// 12/4/2019

package edu.uw.tran6388.ninkawalk

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.math.roundToInt
import androidx.lifecycle.ViewModelProviders
import edu.uw.tran6388.ninkawalk.ui.notifications.NotificationsViewModel
import com.google.gson.Gson

// Enables a sensor to get data from a pedometer
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
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
    }

    // Makes a saved instance
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(QUERY_STRING, totalPoints)
        super.onSaveInstanceState(outState)
    }

    // Resumes the counting
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

    // Stops the sensor when a person stops
    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
    }

    // overrides onAccuracyChanged function
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    // Changes the display on home based on the number of steps a person has taken
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