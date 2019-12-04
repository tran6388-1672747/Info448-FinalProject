package edu.uw.tran6388.ninkawalk.ui

import androidx.lifecycle.ViewModel

class ModelStore() {

    /*private val _text = MutableLiveData<String>().apply {
        value = "My Private Collection"
    }
    val text: LiveData<String> = _text*/

    private val pokemonList = mutableListOf<Pokemon>()

    fun addPokemon(pokemon: Pokemon) {
        pokemonList.add(pokemon)
    }

    fun getPokemonList():List<Pokemon> {
        return pokemonList
    }

}