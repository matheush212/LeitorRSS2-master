package br.org.catolicasc.leitorrss;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ParseJson {
    private static final String TAG = "ParseJson";
    private List<Pokemon> pokemons;
    private JSONArray jsonArray;

    public ParseJson() {
        pokemons = new ArrayList<>();
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void parse(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            jsonArray = json.getJSONArray("pokemon");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject pokemon = jsonArray.getJSONObject(i);
                Log.d(TAG, "parse: " + pokemon);
                Pokemon p = new Pokemon(
                        pokemon.getInt("id"),
                        pokemon.getString("name"),
                        pokemon.getString("img"),
                        pokemon.getString("num"),
                        pokemon.getString("height"),
                        pokemon.getString("weight")
                );
                pokemons.add(p);
            }
        } catch (JSONException e) {
            Log.d(TAG, "parse: Erro fazendo parse de String JSON: " + e.getMessage());
        }

    }
}
