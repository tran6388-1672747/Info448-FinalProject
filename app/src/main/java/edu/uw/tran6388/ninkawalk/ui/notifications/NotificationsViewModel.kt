package edu.uw.tran6388.ninkawalk.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.uw.tran6388.ninkawalk.ui.Pokemon

class NotificationsViewModel : ViewModel() {

    /*private val _text = MutableLiveData<String>().apply {
        value = "My Private Collection"
    }
    val text: LiveData<String> = _text*/

    private val pokemonList = mutableListOf<Pokemon>()
    private val map = HashMap<String, Int>()
    var totalPokemon = 0

    fun getPokemonList():List<Pokemon> {
        return pokemonList
    }

    fun addToMap(key: String, pokemon: Pokemon) {
        if (map.containsKey(key)) {
            val newValue = (map.get(key) as Int) + 1
            map.put(key, newValue)
            totalPokemon += 1
        } else {
            map.put(key, 1)
            pokemonList.add(pokemon)
            totalPokemon += 1
        }
    }

    fun getPokemonCountByKey(key: String): Int {
        return map.get(key) as Int
    }

    /*private val pokemons: MutableLiveData<List<Pokemon>> by lazy {
        MutableLiveData().also {
            loadUsers()
        }
    }

    fun getPokemons(): LiveData<List<Pokemon>> {
        return pokemons
    }

    private fun loadUsers() {
        // Do an asynchronous operation to fetch users.
    }*/
}