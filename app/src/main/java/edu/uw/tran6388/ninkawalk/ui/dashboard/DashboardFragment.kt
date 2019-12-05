package edu.uw.tran6388.ninkawalk.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import edu.uw.tran6388.ninkawalk.R

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import edu.uw.tran6388.ninkawalk.ui.Pokemon
import org.json.JSONObject
import kotlinx.android.synthetic.main.one_pokemon_list.view.*
import com.squareup.picasso.Picasso
import edu.uw.tran6388.ninkawalk.DetailActivity
import kotlinx.android.synthetic.main.fragment_dashboard.*
import edu.uw.tran6388.ninkawalk.MainActivity
import edu.uw.tran6388.ninkawalk.ui.ModelStore
import edu.uw.tran6388.ninkawalk.ui.notifications.NotificationsViewModel

// The helper class to be call when creating adapter.
class Helper {
    companion object {
        private var queue: RequestQueue? = null

        fun dataRequestQueue(context: Context): RequestQueue? {
            queue = Volley.newRequestQueue(context)
            return queue
        }
    }
}


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var notificationViewModel: NotificationsViewModel
    private var listOfPokemon = mutableListOf<Pokemon>()
    private val listOfPokemonName = mutableListOf<String>()
    private var queue: RequestQueue? = null
    private val twoPane = false
    private var totalPoint = 0
    lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        /*notificationViewModel =
            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)*/

        //modelStore = ModelStore()

        notificationsViewModel = (activity as MainActivity).notificationsViewModel

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)


        val context = getActivity()?.getApplicationContext() as Context
        queue = Helper.dataRequestQueue(context)

        val recyclerView = root.findViewById<RecyclerView>(R.id.pokemon_list);
        var steps = (activity as MainActivity).steps
        if(steps != null)
            root.findViewById<TextView>(R.id.number_of_points).setText("$steps Steps")
        getListOfPokemonName(recyclerView)


        //Log.v("onCreateView: ", "in the shop fragment")

        return root
    }

    private fun getListOfPokemonName(recyclerView: RecyclerView) {
        val url = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/pokedex.php?pokedex=all"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->


                val separated = response.split("\n")

                for (i in 0..separated.size-1) {
                    val onePokemon = separated[i].split(":")
                    listOfPokemonName.add(onePokemon[1])
                }

                val i = 0
                getPokemon(recyclerView, i)
            },
            Response.ErrorListener { error ->
                //textView.text = error.toString()
                Log.v("Error PokemonName:", error.toString())
            }
        )
        queue?.add(stringReq)
    }

    private fun getPokemon(recyclerView: RecyclerView, index: Int) {

        // making a API call information for one pokemon.
        val url2 = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/pokedex.php?pokemon=" + listOfPokemonName[index]
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url2, null,
            Response.Listener { response ->


                val pokemon = parseForOnePokemon(response)
                listOfPokemon.add(pokemon)


                if (index < 10) {
                    getPokemon(recyclerView, index + 1)
                } else if (index == 10) {
                    // Create the adapter to convert the array to views
                    recyclerView.adapter = SimpleItemRecyclerViewAdapter(getActivity() as MainActivity, listOfPokemon, twoPane, totalPoint,
                        dashboardViewModel, notificationsViewModel, { partItem : Pokemon -> pokemonClicked(partItem)})

                    //recyclerView.adapter = SimpleItemRecyclerViewAdapter(getActivity() as FragmentActivity, listOfPokemon, twoPane, { partItem : Pokemon -> pokemonClicked(partItem)})
                }
            },
            Response.ErrorListener { error ->
                //textView.text = error.toString()
                Log.v("Error Pokemon:", error.toString())
            }
        )
        queue?.add(jsonObjectRequest)
    }

    fun pokemonClicked(pokemon: Pokemon){
        val cost = pokemon.id*100
        if ((activity as MainActivity).steps < cost) {
            Toast. makeText(getActivity()?.getApplicationContext() as Context,"Sorry, Not enough points.",Toast.LENGTH_LONG).show()
        } else {

            if (notificationsViewModel.totalPokemon >= 50) {
                Toast. makeText(getActivity()?.getApplicationContext() as Context,"Sorry, the limit of collection list is 50 pokemons.",Toast.LENGTH_LONG).show()
            } else {
                (activity as MainActivity).steps -= cost
                notificationsViewModel.addToMap(pokemon.name, pokemon)
                updateScoreStore()
                Toast. makeText(getActivity()?.getApplicationContext() as Context,"Great, You have bought the pokemon!!. The remaining points: " + (activity as MainActivity).steps,Toast.LENGTH_LONG).show();
            }
        }
    }

    fun updateScoreStore(){
        var steps = (activity as MainActivity).steps
        number_of_points.setText("$steps Steps")


    }

    /**
     * Parses the query response from the News API aggregator
     * https://newsapi.org/
     */
    fun parseForOnePokemon(response: JSONObject): Pokemon {

        val name = response.getString("name")
        val hp = response.getInt("hp").toString()

        val infoJSON = response.getJSONObject("info")
        val id = infoJSON.getInt("id")
        val type = infoJSON.getString("type")
        val weakness = infoJSON.getString("weakness")
        val description = infoJSON.getString("description")

        val image = response.getJSONObject("images").getString("photo").split("/")
        val imageURL = image[1].split(".")[0] + ".png"

        val pokemon = Pokemon(
            name = name,
            description = description,
            id = id,
            hp = hp,
            type = type,
            weakness = weakness,
            imageURL = imageURL
        )

        return pokemon
    }


    // Create the adapter.
    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: FragmentActivity,
        private val values: List<Pokemon>,
        private val twoPane: Boolean,
        private var totalPoint: Int,
        private val dashboardViewModel: DashboardViewModel,
        private val notificationsViewModel: NotificationsViewModel,
        private val clickListener: (Pokemon) -> Unit

        //val clickListener: (Pokemon) -> Unit
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                showDetailsView(v)
            }
        }

        // method to be call by onclickListener to set listener for each fragment.
        private fun showDetailsView(v: View) {
            val item = v.tag as Pokemon
            if (twoPane) {

                // This is for passing the item
                /*val fragment = ItemDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("item_frag", item)
                    }
                }

                parentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .addToBackStack(null)
                    .commit()*/

                // This is for extra credit.
                /*val frag = ItemDetailFragment.newInstance(item.headline,
                    item.description, item.sourceName)

                parentActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.item_detail_container, frag)
                    .addToBackStack(null)
                    .commit()*/

            } else {
                /*val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                    putExtra("item_frag", item)
                }
                v.context.startActivity(intent)*/
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.one_pokemon_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(values[position], clickListener)
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(part: Pokemon, clickListener: (Pokemon) -> Unit) {
                itemView.view_name.text = part.name
                itemView.view_cost.text = "Cost: " + (part.id.toInt() * 100).toString() + " steps."
                val url = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/sprites/" + part.imageURL
                Picasso.get().load(url).into(itemView.view_image)
                itemView.buy_button.setOnClickListener {
                    clickListener(part)
                }
                itemView.btn_detail.setOnClickListener {  val intent = Intent(it.context, DetailActivity::class.java).apply {
                    putExtra("item_id", part)
                }
                    it.context.startActivity(intent)}
            }
        }
    }
}


//OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO


// The helper class to be call when creating adapter.
/*
class Helper {
    companion object {
        private var queue: RequestQueue? = null

        fun dataRequestQueue(context: Context): RequestQueue? {
            queue = Volley.newRequestQueue(context)
            return queue
        }
    }
}


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var listOfPokemon = mutableListOf<Pokemon>()
    private val listOfPokemonName = mutableListOf<String>()
    private var queue: RequestQueue? = null
    private val twoPane = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val context = getActivity()?.getApplicationContext() as Context
        queue = Helper.dataRequestQueue(context)

        val recyclerView = root.findViewById<RecyclerView>(R.id.pokemon_list);
        var steps = (activity as MainActivity).steps
        if(steps != null)
            root.findViewById<TextView>(R.id.number_of_points).setText("$steps Steps")
        getListOfPokemonName(recyclerView)

        return root
    }

    private fun getListOfPokemonName(recyclerView: RecyclerView) {
        val url = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/pokedex.php?pokedex=all"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->


                val separated = response.split("\n")

                for (i in 0..separated.size-1) {
                    val onePokemon = separated[i].split(":")
                    listOfPokemonName.add(onePokemon[1])
                }

                val i = 0
                getPokemon(recyclerView, i)
            },
            Response.ErrorListener { error ->
                //textView.text = error.toString()
                Log.v("Error PokemonName:", error.toString())
            }
        )
        queue?.add(stringReq)
    }

    private fun getPokemon(recyclerView: RecyclerView, index: Int) {

        // making a API call information for one pokemon.
        val url2 = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/pokedex.php?pokemon=" + listOfPokemonName[index]
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url2, null,
            Response.Listener { response ->

                val pokemon = parseForOnePokemon(response)
                listOfPokemon.add(pokemon)

                Log.v("current acivity: ", getActivity().toString())

                if (index < 10) {
                    getPokemon(recyclerView, index + 1)
                } else if (index == 10) {
                    // Create the adapter to convert the array to views
                    recyclerView.adapter = ShopAdapter(getActivity() as FragmentActivity, listOfPokemon, twoPane, { partItem : Pokemon -> pokemonClicked(partItem)})
                }
            },
            Response.ErrorListener { error ->
                //textView.text = error.toString()
                Log.v("Error Pokemon:", error.toString())
            }
        )
        queue?.add(jsonObjectRequest)
    }

    fun pokemonClicked(pokemon: Pokemon){
        if ((activity as MainActivity).steps < (pokemon.id.toInt() * 100)){
            Toast.makeText(getActivity()?.getApplicationContext() as Context, "NOT ENOUGH POINTS", Toast.LENGTH_LONG).show()
        } else {
            if((activity as MainActivity).collectionPokemon.containsKey(pokemon.id.toInt())){
                var count = (activity as MainActivity).collectionPokemon[pokemon.id.toInt()]!!
                ++count
                (activity as MainActivity).collectionPokemon[pokemon.id.toInt()] = count
            } else {
                (activity as MainActivity).collectionPokemon.put(pokemon.id.toInt(), 1)
            }
            (activity as MainActivity).steps -= (pokemon.id.toInt() * 100)

            updateScoreStore()
        }
    }

    fun updateScoreStore(){
        var steps = (activity as MainActivity).steps
        number_of_points.setText("$steps Steps")
    }





    /**
     * Parses the query response from the News API aggregator
     * https://newsapi.org/
     */
    fun parseForOnePokemon(response: JSONObject): Pokemon {
        val name = response.getString("name")
        val hp = response.getInt("hp").toString()

        val infoJSON = response.getJSONObject("info")
        val id = infoJSON.getInt("id").toString()
        val type = infoJSON.getString("type")
        val weakness = infoJSON.getString("weakness")
        val description = infoJSON.getString("description")

        val image = response.getJSONObject("images").getString("photo").split("/")
        val imageURL = image[1].split(".")[0] + ".png"

        val pokemon = Pokemon(
            name = name,
            description = description,
            id = id,
            hp = hp,
            type = type,
            weakness = weakness,
            imageURL = imageURL
        )

        return pokemon
    }


    // Create the adapter.
    class ShopAdapter(private val parentActivity: FragmentActivity, private val values: List<Pokemon>, private val twoPane: Boolean, val clickListener: (Pokemon) -> Unit
    ) :
        RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                showDetailsView(v)
            }
        }

        // method to be call by onclickListener to set listener for each fragment.
        private fun showDetailsView(v: View) {
            val item = v.tag as Pokemon
            if (twoPane) {

                // This is for passing the item
                /*val fragment = ItemDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("item_frag", item)
                    }
                }
                parentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .addToBackStack(null)
                    .commit()*/

                // This is for extra credit.
                /*val frag = ItemDetailFragment.newInstance(item.headline,
                    item.description, item.sourceName)
                parentActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.item_detail_container, frag)
                    .addToBackStack(null)
                    .commit()*/

            } else {
                /*val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                    putExtra("item_frag", item)
                }
                v.context.startActivity(intent)*/
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.one_pokemon_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(values[position], clickListener)
        }

        override fun getItemCount() = values.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(part: Pokemon, clickListener: (Pokemon) -> Unit) {
                itemView.view_name.text = part.name
                itemView.view_cost.text = "Cost: " + (part.id.toInt() * 100).toString() + " steps."
                val url = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/sprites/" + part.imageURL
                Picasso.get().load(url).into(itemView.view_image)
                itemView.buy_button.setOnClickListener { clickListener(part)}
            }
        }
    }
}
*/