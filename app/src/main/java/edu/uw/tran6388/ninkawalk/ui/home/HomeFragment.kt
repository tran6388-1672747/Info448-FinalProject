// Joon Chang, William Fu, Jimmy Tran
// 12/4/2019

package edu.uw.tran6388.ninkawalk.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.uw.tran6388.ninkawalk.R
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.android.synthetic.main.fragment_home.*
import edu.uw.tran6388.ninkawalk.MainActivity
import kotlin.math.roundToInt

// A fragment for home to use
class HomeFragment : Fragment(), SensorEventListener {

    private lateinit var homeViewModel: HomeViewModel
    var sensorManager:SensorManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val counter: TextView = root.findViewById(R.id.text_display)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        var steps = (activity as MainActivity).steps.toString()
        // As long as it's not null, set the text
        if (steps != null)
            counter.setText(steps)
        sensorManager = (activity as MainActivity).sensorManager
        return root
    }

    // Overrides onAccuracyChanged function
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // Actively updates the number display even when the home is restored
    override fun onSensorChanged(event: SensorEvent) {
        Log.d("onSensorChanged", "Updating Steps")
        (activity as MainActivity).previousSteps = event.values[0].roundToInt()
        (activity as MainActivity).steps += (activity as MainActivity).previousSteps - (activity as MainActivity).previousSteps2
        (activity as MainActivity).previousSteps2 = (activity as MainActivity).previousSteps
        text_display.text = (activity as MainActivity).steps.toString()
    }
}