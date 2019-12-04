package edu.uw.tran6388.ninkawalk

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.squareup.picasso.Picasso
import edu.uw.tran6388.ninkawalk.ui.Pokemon
import kotlinx.android.synthetic.main.detail_activity.*

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [DetailActivity].
 */
class DetailActivity : AppCompatActivity() {

    private var item: Pokemon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        item = getIntent().getParcelableExtra<Pokemon>("item_id")

        val url = "https://courses.cs.washington.edu/courses/cse154/webservices/pokedex/sprites/" +  item?.imageURL

        try {
            Picasso.get().load(url).into(item_detail_image)

        } catch (e: Exception) {
            item_detail_image.setImageResource(R.drawable.broken_links)
        }

        detail_name.text = item?.name
        detail_description.text = "DESCRIPTION: " + item?.description
        detail_hp.text = "HP: " + item?.hp
        detail_type.text = "TYPE: " + item?.type
        detail_weakness.text = "WEAKNESS: " + item?.weakness



    }
}