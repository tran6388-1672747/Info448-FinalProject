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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import edu.uw.tran6388.ninkawalk.R

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.google.android.material.appbar.CollapsingToolbarLayout
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

// The helper class to be call when creating adapter.
class Hepler {
    companion object {
        private var queue: RequestQueue? = null

        //kotlin
        //instantiate the image loader (from Kotlin docs)
        //params are the requestQueue and the Cache
        /*val imageLoader: ImageLoader by lazy { //only instantiate when needed
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
        }*/

        fun dataRequestQueue(context: Context): RequestQueue? {
            queue = Volley.newRequestQueue(context)
            return queue
        }
    }
}


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private val listOfPokemon = mutableListOf<Pokemon>()
    private val listOfPokemonName = mutableListOf<String>()
    private var queue: RequestQueue? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        /*dashboardViewModel.text.observe(this, Observer {
            textView.text = it
        })*/





        val context = getActivity()?.getApplicationContext() as Context
        queue = Hepler.dataRequestQueue(context)

        getListOfPokemonName(textView)
        //getListOfPokemon(textView)

        return root
    }

    private fun getListOfPokemonName(textView: TextView) {
        val url = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/pokedex.php?pokedex=all"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->

                val separated = response.split("\n")

                for (i in 0..separated.size) {
                    val onePokemon = separated[0].split(":")
                    listOfPokemonName.add(onePokemon[1])
                }



                getListOfPokemon(textView, listOfPokemonName[0])
            },
            Response.ErrorListener { error ->
                textView.text = error.toString()
            }
        )
        queue?.add(stringReq)
    }

    private fun getListOfPokemon(textView: TextView, pokemonName: String) {

        // making a API call information for one pokemon.
        val url2 = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/pokedex.php?pokemon=" + pokemonName
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

                textView.text = response.toString()


            },
            Response.ErrorListener { error ->
                textView.text = error.toString()
            }
        )
        queue?.add(jsonObjectRequest)
    }



    /**
     * Parses the query response from the News API aggregator
     * https://newsapi.org/
     */
    /*fun parseForOnePokemon(response: JSONObject): Pokemon {

        //val stories = mutableListOf<Pokemon>()

        //val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        try {
            val jsonArticles = response.getJSONArray("articles") //response.articles

            for (i in 0 until Math.min(jsonArticles.length(), 20)) { //stop at 20
                val articleItemObj = jsonArticles.getJSONObject(i)

                //handle image url
                var imageUrl:String? = articleItemObj.getString("urlToImage")
                if (imageUrl == "null" || !URLUtil.isValidUrl(imageUrl)) {
                    imageUrl = null //make actual null value
                }

                //handle date
                val publishedTime = try {
                    val pubDateString = articleItemObj.getString("publishedAt")
                    if(pubDateString != "null")
                        formatter.parse(pubDateString).time
                    else
                        0L //return 0
                } catch (e: ParseException) {
                    Log.e("Pokemon JSONOject: ", "Error parsing date", e) //Android log the error
                    0L //return 0
                }

                //access source
                val sourceObj = articleItemObj.getJSONObject("source")

                val story = Pokemon(
                    headline = articleItemObj.getString("title"),
                    webUrl = articleItemObj.getString("url"),
                    description = articleItemObj.getString("description"),
                    imageUrl = imageUrl,
                    publishedTime = publishedTime,
                    sourceId = sourceObj.getString("id"),
                    sourceName = sourceObj.getString("name")
                )

                stories.add(story)
            } //end for loop
        } catch (e: JSONException) {
            Log.e("Pokemon JSONObject", "Error parsing json", e) //Android log the error
        }

        return stories
    }*/

    // pass the request with the url and recyclerview to get back the json list.
    private fun setupRecyclerView(recyclerView: RecyclerView, url: String, context: Context) {

        //val queue = Hepler.dataRequestQueue(getActivity()?.getApplicationContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->

                //val list = DummyContent.parseNewsAPI(response)

                // Set the grid column to 2 for small scene.
                /*if (!twoPane) {
                    val numberOfColumns = 2;
                    recyclerView.setLayoutManager(GridLayoutManager(this, numberOfColumns))
                }*/

                // Create the adapter to convert the array to views
                //recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, list, twoPane)
            },
            Response.ErrorListener { error ->
                print(error.toString())
            }
        )
        //queue?.add(jsonObjectRequest)
    }

    // Create the adapter.
    /*class SimpleItemRecyclerViewAdapter(
        private val parentActivity: ItemListActivity,
        private val values: List<DummyContent.NewsArticle>,
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
            val item = v.tag as DummyContent.NewsArticle
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
                val frag = ItemDetailFragment.newInstance(item.headline,
                    item.description, item.sourceName)

                parentActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.item_detail_container, frag)
                    .addToBackStack(null)
                    .commit()

            } else {
                val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                    putExtra("item_frag", item)
                }
                v.context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]

            val url = item.imageUrl

            holder.cardImage.setImageUrl(url, DummyContent.Hepler.imageLoader)
            holder.cardImage.setDefaultImageResId(R.drawable.broken_links)
            holder.cardImage.setErrorImageResId(R.drawable.broken_links)

            holder.headline.text = item.headline

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cardImage: NetworkImageView = view.card_view_image
            val headline: TextView = view.card_view_headline
        }
    }*/
}