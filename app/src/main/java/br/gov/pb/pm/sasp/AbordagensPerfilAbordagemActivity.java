package br.gov.pb.pm.sasp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AbordagensPerfilAbordagemActivity extends SaspActivity {

    private DialogHelper dialogHelper;
    private SaspServer saspServer;
    private SupportMapFragment mapFragment;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.abordagens_activity_perfil_abordagem);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        try {

            JSONObject json = DataHolder.getInstance().getAbordagemData();

            double lat = json.getDouble("latitude");
            double lon = json.getDouble("longitude");

            latLng = new LatLng(lat, lon);

            ((TextView)findViewById(R.id.textRelato)).setText(json.getString("relato"));

            JSONArray jsonArray = json.getJSONArray("Imagens");

            final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewImage);

            for (int a = 0; a < jsonArray.length(); a++) {

                final View child = LayoutInflater.from(AbordagensPerfilAbordagemActivity.this).inflate(R.layout.layout_nova_imagem, null);

                vg.addView(child);

                final ImageView novaImagem = child.findViewById(R.id.imageNew);

                final JSONObject jsonObject = jsonArray.getJSONObject(a);

                ImageLoader.getInstance().loadImage(SaspServer.getImageAddress(jsonObject.getString("img_busca"), SaspImage.UPLOAD_OBJECT_MODULO_ABORDAGENS, true), new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                        novaImagem.setImageResource(R.drawable.icon_images);

                        super.onLoadingStarted(imageUri, view);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        novaImagem.setImageBitmap(loadedImage);

                        super.onLoadingComplete(imageUri, view, loadedImage);
                    }
                });

                child.findViewById(R.id.imageClick).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        try {

                            Intent i = new Intent(AbordagensPerfilAbordagemActivity.this, ImageViewActivity.class);
                            i.putExtra("img_principal", jsonObject.getString("img_principal"));
                            i.putExtra("modulo", SaspImage.UPLOAD_OBJECT_MODULO_ABORDAGENS);
                            startActivity(i);
                        }
                        catch (Exception e) { }
                    }
                });

                /*child.findViewById(R.id.imageClick).setOnLongClickListener(new View.OnLongClickListener() {

                            @Override
                            public boolean onLongClick(View view) {

                                String[] opcoes = {"Excluir imagem"};

                                dialogHelper.showList("Imagem", opcoes, new MaterialDialog.ListCallback() {

                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                                        try {

                                            if (DataHolder.getInstance().getLoginData("id_usuario").equals(jsonObject.getString("id_usuario"))) {

                                                dialogHelper.showProgress();

                                                saspServer.excluirImagemSuspeito(DataHolder.getInstance().getPessoaDataItem("id_suspeito"), jsonObject.getString("img_principal"), new SincabsResponse() {

                                                    @Override
                                                    void onResponseNoError(String msg, JSONObject extra) {

                                                        new Handler().postDelayed(new Runnable() {

                                                            @Override
                                                            public void run() {

                                                                vg.removeView(child);
                                                            }
                                                        }, 500);
                                                    }

                                                    @Override
                                                    void onResponseError(String error) {

                                                        dialogHelper.showError(error);
                                                    }

                                                    @Override
                                                    void onNoResponse(String error) {

                                                        dialogHelper.showError(error);
                                                    }

                                                    @Override
                                                    void onPostResponse() {

                                                        dialogHelper.dismissProgress();
                                                    }
                                                });

                                            }
                                            else {

                                                dialogHelper.showError("Você não pode excluir uma imagem que foi adicionada por outro usuário.");
                                            }
                                        }
                                        catch (Exception e) { }
                                    }
                                });

                                return false;
                            }
                        });*/
            }

            final ViewGroup vg2 = (ViewGroup)findViewById(R.id.layoutPessoasAbordadas);

            JSONArray jsonArray2 = json.getJSONArray("Pessoas");

            for (int a = 0; a < jsonArray2.length(); a++) {

                JSONObject j = jsonArray2.getJSONObject(a);

                final String id_pessoa = j.getString("id_pessoa");

                final View child = LayoutInflater.from(AbordagensPerfilAbordagemActivity.this).inflate(R.layout.layout_lista_pessoas2, null);

                child.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        dialogHelper.showProgress();

                        saspServer.pessoasPerfil(id_pessoa, new SaspResponse(AbordagensPerfilAbordagemActivity.this) {

                            @Override
                            void onSaspResponse(String error, String msg, JSONObject extra) {

                                DataHolder.getInstance().setPessoaData(extra);

                                Intent i = new Intent(AbordagensPerfilAbordagemActivity.this, PessoasPerfilPessoaActivity.class);
                                startActivityForResult(i, 400);
                            }

                            @Override
                            void onResponse(String error) {

                                dialogHelper.showError(error);
                            }

                            @Override
                            void onNoResponse(String error) {

                                dialogHelper.showError(error);
                            }

                            @Override
                            void onPostResponse() {

                                dialogHelper.dismissProgress();
                            }
                        });
                    }
                });

                vg2.addView(child);

                ((TextView)child.findViewById(R.id.nomeAlcunha)).setText(j.getString("nome_completo"));
                ((TextView)child.findViewById(R.id.areasAtuacao)).setText(j.getString("areas_de_atuacao"));
                ((TextView)child.findViewById(R.id.dataCadastro)).setText(j.getString("data_registro"));

                final ImageView novaImagem = child.findViewById(R.id.imagemPerfil);

                ImageLoader.getInstance().loadImage(SaspServer.getImageAddress(j.getString("img_busca"), SaspImage.UPLOAD_OBJECT_MODULO_PESSOAS, true), new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                        novaImagem.setImageResource(R.drawable.icon_images);

                        super.onLoadingStarted(imageUri, view);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        novaImagem.setImageBitmap(loadedImage);

                        super.onLoadingComplete(imageUri, view, loadedImage);
                    }
                });
            }
        }
        catch (Exception e) {

            if (AppUtils.DEBUG_MODE) Toast.makeText(AbordagensPerfilAbordagemActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(final GoogleMap googleMap) {

                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                if (latLng != null) {

                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, 12f);
                    googleMap.moveCamera(center);
                }
            }
        });

        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {

                if (latLng != null) {

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {

                        String ender = "";

                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String estado = addresses.get(0).getAdminArea();

                        ender += address;// + ", " + city; //+ ", " + estado;

                        final String addr = ender;

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                ((TextView)findViewById(R.id.textEndereco)).setText(addr);
                            }
                        });

                    } catch (Exception e) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                ((TextView)findViewById(R.id.textEndereco)).setText("Endereço não confirmado.");
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    public void fecharJanela(View view) {

        finish();
    }
}
