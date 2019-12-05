package edu.uw.tran6388.ninkawalk.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.uw.tran6388.ninkawalk.MainActivity
import edu.uw.tran6388.ninkawalk.R
import edu.uw.tran6388.ninkawalk.ui.dashboard.DashboardViewModel

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import edu.uw.tran6388.ninkawalk.ui.Pokemon
import org.json.JSONObject
import kotlinx.android.synthetic.main.one_pokemon_list.view.*
import edu.uw.tran6388.ninkawalk.DetailActivity
import edu.uw.tran6388.ninkawalk.ui.ModelStore
import edu.uw.tran6388.ninkawalk.ui.notifications.NotificationsViewModel
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.one_pokemon_list.view.btn_detail
import kotlinx.android.synthetic.main.one_pokemon_list.view.view_cost
import kotlinx.android.synthetic.main.one_pokemon_list.view.view_image
import kotlinx.android.synthetic.main.one_pokemon_list.view.view_name
import kotlinx.android.synthetic.main.one_pokemon_list_collection.view.*

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private val twoPane = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*notificationsViewModel =
            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)*/
        notificationsViewModel = (activity as MainActivity).notificationsViewModel
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val pokemonList = notificationsViewModel.getPokemonList()

        val textView: TextView = root.findViewById(R.id.text_notifications)
        if (pokemonList.isEmpty()) {
            textView.text = "No Pokemon in Collection"
            Log.v("into if: ", "into if")
        } else {
            textView.text = "The Collection of Pokemon"
            val recyclerView = root.findViewById<RecyclerView>(R.id.pokemon_list_collection)
            recyclerView.adapter = SimpleItemRecyclerViewAdapter(getActivity() as MainActivity, pokemonList, twoPane, notificationsViewModel)
            Log.v("into else: ", "into else")
        }

        return root
    }

    // Create the adapter.
    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: MainActivity,
        private val values: List<Pokemon>,
        private val twoPane: Boolean,
        private val notificationsViewModel: NotificationsViewModel
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.one_pokemon_list_collection, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]

            val url = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/sprites/" + item.imageURL

            Picasso.get().load(url).into(holder.cardImage)

            holder.name.text = item.name
            holder.cost.text = "Cost: " + (item.id * 100).toString() + " points."
            holder.count.text = "Counts: " + notificationsViewModel.getPokemonCountByKey(item.name)

            holder.detailButton.setOnClickListener {

                val intent = Intent(it.context, DetailActivity::class.java).apply {
                    putExtra("item_id", item)
                }
                it.context.startActivity(intent)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cardImage: ImageView = view.view_image
            val name: TextView = view.view_name
            val cost: TextView = view.view_cost
            val count: TextView = view.view_count
            val detailButton: Button = view.btn_detail
        }
    }
}
