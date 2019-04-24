package br.org.catolicasc.leitorrss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PokemonAdapter extends ArrayAdapter {
    private static final String TAG = "PokemonAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater; // Faz conversão layout resource em objeto
    private List<Pokemon> pokemon; //Lista que passa pro parametro la na aplicação.
    private Bitmap imagem;

    public PokemonAdapter(Context context, int resource, List<Pokemon> aplicativos) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.pokemon = aplicativos;
    }

    @Override
    public int getCount() {
        return pokemon.size();
    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int resposta = connection.getResponseCode();

                if (resposta != HttpURLConnection.HTTP_OK) { // se resposta não foi OK
                    if (resposta == HttpURLConnection.HTTP_MOVED_TEMP  // se for um redirect
                            || resposta == HttpURLConnection.HTTP_MOVED_PERM
                            || resposta == HttpURLConnection.HTTP_SEE_OTHER) {
                        // pegamos a nova URL e abrimos nova conexão!
                        String novaUrl = connection.getHeaderField("Location");
                        connection = (HttpURLConnection) new URL(novaUrl).openConnection();
                    }
                }
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Erro ao baixar imagem"
                        + e.getMessage());
            }

            return null;
        }
    }

    public void downloadImagem(String imgUrl) {
        ImageDownloader imageDownloader = new ImageDownloader();

        try {
            // baixar a imagem da internet
            imagem = imageDownloader.execute(imgUrl).get();
            // atribuir a imagem ao imageView
        } catch (Exception e) {
            Log.e(TAG, "downloadImagem: Impossível baixar imagem"
                    + e.getMessage());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            Log.d(TAG, "getView: chamada com um convertView null");
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            Log.d(TAG, "getView: recebeu um convertView");
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Pokemon appAtual = pokemon.get(position);

        viewHolder.tvID.setText(String.valueOf(appAtual.getId()));
        viewHolder.tvNome.setText(appAtual.getNome());
        viewHolder.tvNumero.setText(appAtual.getNumero());
        viewHolder.tvAltura.setText(appAtual.getAltura());
        viewHolder.tvPeso.setText(appAtual.getPeso());
        downloadImagem(appAtual.getImgUrl());
        viewHolder.ivAppImg.setImageBitmap(imagem);

        return convertView;
    }

    private class ViewHolder { //mudar os componentes para o pokemon
        final TextView tvID;
        final TextView tvNome;
        final TextView tvNumero;
        final TextView tvAltura;
        final TextView tvPeso;
        final ImageView ivAppImg;

        ViewHolder(View v) {
            this.tvID = v.findViewById(R.id.tvID);
            this.tvNome = v.findViewById(R.id.tvNome);
            this.tvNumero = v.findViewById(R.id.tvNumero);
            this.tvAltura = v.findViewById(R.id.tvAltura);
            this.tvPeso = v.findViewById(R.id.tvPeso);
            this.ivAppImg = v.findViewById(R.id.ivAppImg);
        }
    }
}
