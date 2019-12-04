package edu.uw.tran6388.ninkawalk.ui.dashboard

import android.app.DownloadManager
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import edu.uw.tran6388.ninkawalk.R

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import edu.uw.tran6388.ninkawalk.MainActivity
import edu.uw.tran6388.ninkawalk.ui.Pokemon
import kotlinx.android.parcel.Parcelize
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.one_pokemon_list.view.*
import kotlinx.android.synthetic.main.pokemon_list.*
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

// The helper class to be call when creating adapter.
class Hepler {
    companion object {
        private var queue: RequestQueue? = null

        //kotlin
        //instantiate the image loader (from Kotlin docs)
        //params are the requestQueue and the Cache
        val imageLoader: ImageLoader by lazy { //only instantiate when needed
            ImageLoader(queue,
                object : ImageLoader.ImageCache { //anonymous cache object
                    private val cache = LruCache<String, Bitmap>(20)
                    override fun getBitmap(url: String): Bitmap? {
                        return cache.get(url)
                    }
                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        cache.put(url, bitmap)
                    }
                })
        }

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
        //val textView: TextView = root.findViewById(R.id.text_dashboard)
        /*dashboardViewModel.text.observe(this, Observer {
            textView.text = it
        })*/


        val context = getActivity()?.getApplicationContext() as Context
        queue = Hepler.dataRequestQueue(context)

        val recyclerView = root.findViewById<RecyclerView>(R.id.pokemon_list);
        var steps = (activity as MainActivity).steps
        if(steps != null)
            root.findViewById<TextView>(R.id.number_of_points).setText("$steps STEPS")
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

                //val list = DummyContent.parseNewsAPI(response)

                // Set the grid column to 2 for small scene.
                /*if (!twoPane) {
                    val numberOfColumns = 2;
                    recyclerView.setLayoutManager(GridLayoutManager(this, numberOfColumns))
                }*/

                // Create the adapter to convert the array to views
                //recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, list, twoPane)

                //val x = response.getJSONObject("images").getString("photo").split("/")

                //textView.text = x[1]

                val pokemon = parseForOnePokemon(response)
                listOfPokemon.add(pokemon)

                /*Log.v("index", index.toString())
                Log.v("listOfPokemon", listOfPokemon[index].name)
                Log.v("listOfPokemon", listOfPokemon[index].description)*/

                if (index < 10) {
                    getPokemon(recyclerView, index + 1)
                } else if (index == 10) {
                    // Create the adapter to convert the array to views
                    recyclerView.adapter = SimpleItemRecyclerViewAdapter(getActivity() as FragmentActivity, listOfPokemon, twoPane)
                }
            },
            Response.ErrorListener { error ->
                //textView.text = error.toString()
                Log.v("Error Pokemon:", error.toString())
            }
        )
        queue?.add(jsonObjectRequest)
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
    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: FragmentActivity,
        private val values: List<Pokemon>,
        private val twoPane: Boolean
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
            val item = values[position]

            val url = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/sprites/" + item.imageURL

            /*holder.cardImage.setImageUrl(url, Hepler.imageLoader)
            holder.cardImage.setDefaultImageResId(R.drawable.broken_links)
            holder.cardImage.setErrorImageResId(R.drawable.broken_links)*/

            //Log.v("image url: ", url)

            Picasso.get().load(url).into(holder.cardImage)

            holder.name.text = item.name
            holder.cost.text = "Cost: " + (item.id.toInt() * 100).toString() + " points."

            /*with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }*/
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cardImage: ImageView = view.view_image
            val name: TextView = view.view_name
            val cost: TextView = view.view_cost
        }
    }
}