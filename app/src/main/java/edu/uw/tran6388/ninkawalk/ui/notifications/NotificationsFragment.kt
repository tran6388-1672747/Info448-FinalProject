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
import edu.uw.tran6388.ninkawalk.ui.Pokemon
import kotlinx.android.synthetic.main.collection_pokemon.view.*

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        notificationsViewModel.text.observe(this, Observer {
            textView.text = it
        })

        return root
    }

    // Create the adapter.
    class CollectionAdapter(private val parentActivity: FragmentActivity, private val values: List<Pokemon>, private val twoPane: Boolean, val clickListener: (Pokemon) -> Unit
    ) :
        RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

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


            } else {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_pokemon, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(values[position], clickListener)
        }

        override fun getItemCount() = values.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(part: Pokemon, clickListener: (Pokemon) -> Unit) {
                itemView.view_name.text = part.name
                val url = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/sprites/" + part.imageURL
                Picasso.get().load(url).into(itemView.view_image)
                itemView.setOnClickListener{clickListener(part)}
            }
        }
    }
}