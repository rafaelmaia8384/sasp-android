package br.gov.pb.pm.sasp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class VeiculosPerfilVeiculoActivity extends SaspActivity {

    private DialogHelper dialogHelper;
    private SaspServer saspServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.veiculos_activity_perfil_veiculo);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        try {

            final JSONObject json = DataHolder.getInstance().getPerfilVeiculoData();

            if (!json.isNull("Imagens")) {

                JSONArray jsonArray = json.getJSONArray("Imagens");

                final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewImage);

                for (int a = 0; a < jsonArray.length(); a++) {

                    final View child = LayoutInflater.from(VeiculosPerfilVeiculoActivity.this).inflate(R.layout.layout_nova_imagem, null);

                    vg.addView(child);

                    final ImageView novaImagem = child.findViewById(R.id.imageNew);

                    final JSONObject jsonObject = jsonArray.getJSONObject(a);

                    ImageLoader.getInstance().loadImage(SaspServer.getImageAddress(jsonObject.getString("img_busca"), SaspImage.UPLOAD_OBJECT_MODULO_VEICULOS, true), new SimpleImageLoadingListener() {

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

                                Intent i = new Intent(VeiculosPerfilVeiculoActivity.this, ImageViewActivity.class);
                                i.putExtra("img_principal", jsonObject.getString("img_principal"));
                                i.putExtra("modulo", SaspImage.UPLOAD_OBJECT_MODULO_VEICULOS);
                                startActivity(i);
                            }
                            catch (Exception e) { }
                        }
                    });
                }
            }

            if (!json.isNull("Abordagens")) {

                final ViewGroup vg2 = (ViewGroup)findViewById(R.id.layoutAbordagensRecentes);

                JSONArray jsonArray2 = json.getJSONArray("Abordagens");

                findViewById(R.id.textAbordagemRecente).setVisibility(View.GONE);

                for (int a = 0; a < jsonArray2.length(); a++) {

                    JSONObject j = jsonArray2.getJSONObject(a);

                    final String id_abordagem = j.getString("id_abordagem");

                    final View child = LayoutInflater.from(VeiculosPerfilVeiculoActivity.this).inflate(R.layout.layout_lista_abordagens2, null);

                    child.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            dialogHelper.showProgress();

                            saspServer.abordagensPerfil(id_abordagem, new SaspResponse(VeiculosPerfilVeiculoActivity.this) {

                                @Override
                                void onSaspResponse(String error, String msg, JSONObject extra) {

                                    DataHolder.getInstance().setAbordagemData(extra);

                                    Intent i = new Intent(VeiculosPerfilVeiculoActivity.this, AbordagensPerfilAbordagemActivity.class);
                                    startActivity(i);
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

                    ((TextView)child.findViewById(R.id.textNumeroAbordados)).setText(j.getString("numero_abordados"));
                    ((TextView)child.findViewById(R.id.textGPS)).setText(j.getString("latitude") + ", " + j.getString("longitude"));
                    ((TextView)child.findViewById(R.id.dataCadastro)).setText(AppUtils.formatarData(j.getString("data_registro")));

                    final ImageView novaImagem = child.findViewById(R.id.imagemPerfil);

                    ImageLoader.getInstance().loadImage(SaspServer.getImageAddress(j.getString("img_busca"), SaspImage.UPLOAD_OBJECT_MODULO_ABORDAGENS, true), new SimpleImageLoadingListener() {

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

            if (json.getString("tipo_placa").equals("2")) {

                findViewById(R.id.placaNormal).setVisibility(View.GONE);
                findViewById(R.id.placaMercosul).setVisibility(View.VISIBLE);
            }

            ((TextView)findViewById(R.id.textPlaca)).setText(json.getString("placa"));
            ((TextView)findViewById(R.id.textDescricao)).setText(json.getString("descricao"));
        }
        catch (Exception e) {

            if (AppUtils.DEBUG_MODE) Toast.makeText(VeiculosPerfilVeiculoActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    public void fecharJanela(View view) {

        finish();
    }
}
