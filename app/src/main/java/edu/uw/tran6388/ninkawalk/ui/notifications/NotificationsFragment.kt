package edu.uw.tran6388.ninkawalk.ui.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.uw.tran6388.ninkawalk.MainActivity
import edu.uw.tran6388.ninkawalk.R

import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.google.gson.Gson
import edu.uw.tran6388.ninkawalk.ui.Pokemon
import edu.uw.tran6388.ninkawalk.DetailActivity
import kotlinx.android.synthetic.main.one_pokemon_list.view.btn_detail
import kotlinx.android.synthetic.main.one_pokemon_list.view.view_cost
import kotlinx.android.synthetic.main.one_pokemon_list.view.view_image
import kotlinx.android.synthetic.main.one_pokemon_list.view.view_name
import kotlinx.android.synthetic.main.one_pokemon_list_collection.view.*

/*
This is a fragment view that will show the collection of pokemon that the users have purchased
from the shop. It will also show in a recycling View.
 */
class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private val twoPane = false

    // To infated the fragment view.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*notificationsViewModel =
            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)*/
        notificationsViewModel = (activity as MainActivity).notificationsViewModel
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        var pokemonList = notificationsViewModel.getPokemonList()

        //val textView: TextView = root.findViewById(R.id.text_notifications)
        if (pokemonList.isEmpty()) {

            // This section of code is to check if there is some collection data stores in the internal storage.
            val mPrefs = (activity as MainActivity).getPreferences(Context.MODE_PRIVATE)
            val gson = Gson()
            val json = mPrefs.getString("collection_list", "")
            val notificationsViewModel2 = gson.fromJson<NotificationsViewModel>(json, NotificationsViewModel::class.java!!)

            // A check condition if the data exite in the storage.
            if (notificationsViewModel2 == null) {
                Log.v("notificationView", "null")
            } else {
                notificationsViewModel = notificationsViewModel2
                pokemonList = notificationsViewModel.getPokemonList()
                val recyclerView = root.findViewById<RecyclerView>(R.id.pokemon_list_collection)
                recyclerView.adapter = SimpleItemRecyclerViewAdapter(getActivity() as MainActivity, pokemonList, twoPane, notificationsViewModel)
                Log.v("into if-else: ", "into if-else")
            }
            Log.v("into if: ", "into if")
        } else {
            // call the adpater to add all the pokemon
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

        // To bind the information of each pokemon for one item in the holder.
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
