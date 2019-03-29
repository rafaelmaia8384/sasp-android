package br.gov.pb.pm.sasp;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class VeiculosCadastrarVeiculoActivity extends SaspActivity {

    public static final int CODE_ACTIVITY_CADASTRAR_VEICULO = 912;

    private DialogHelper dialogHelper;
    private SaspServer saspServer;
    private PopupMenu pmImagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.veiculos_activity_cadastrar_veiculo);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        pmImagem = new PopupMenu(VeiculosCadastrarVeiculoActivity.this, findViewById(R.id.viewAddImage));
        pmImagem.inflate(R.menu.menu_adicionar_imagem);
        pmImagem.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                dialogHelper.showProgress();

                CropImage.activity()
                        .setCropMenuCropButtonTitle("Pronto")
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputCompressQuality(90)
                        .setRequestedSize(840, 840)
                        .start(VeiculosCadastrarVeiculoActivity.this);

                return false;
            }
        });

        final TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {

                    findViewById(R.id.layoutPlacaNormal).setVisibility(View.VISIBLE);
                    findViewById(R.id.layoutPlacaMercosul).setVisibility(View.GONE);
                    findViewById(R.id.editPlacaMercosul).setSelected(false);
                    findViewById(R.id.editPlacaNormal).setSelected(true);
                }
                else {

                    findViewById(R.id.layoutPlacaNormal).setVisibility(View.GONE);
                    findViewById(R.id.layoutPlacaMercosul).setVisibility(View.VISIBLE);
                    findViewById(R.id.editPlacaMercosul).setSelected(true);
                    findViewById(R.id.editPlacaNormal).setSelected(false);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    public void fecharJanela(View view) {

        finish();
    }

    public void buttonAdicionarImagem(View view) {

        pmImagem.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            dialogHelper.dismissProgress();

            if (resultCode == RESULT_OK) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewImage);

                final View child = LayoutInflater.from(VeiculosCadastrarVeiculoActivity.this).inflate(R.layout.layout_nova_imagem, null);

                child.findViewById(R.id.imageClick).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        PopupMenu pop = new PopupMenu(VeiculosCadastrarVeiculoActivity.this, view);
                        pop.inflate(R.menu.menu_abordagens_imagem);

                        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                if (menuItem.getOrder() == 1) {

                                    YoYo.with(Techniques.ZoomOut)
                                            .duration(500)
                                            .onEnd(new YoYo.AnimatorCallback() {

                                                @Override
                                                public void call(Animator animator) {

                                                    vg.removeView(child);
                                                }
                                            })
                                            .playOn(child);
                                }

                                return false;
                            }
                        });

                        pop.show();
                    }
                });

                vg.addView(child, 0);

                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(child);

                ImageView novaImagem = child.findViewById(R.id.imageNew);

                novaImagem.setTag(result.getUri());
                novaImagem.setImageURI(result.getUri());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void buttonCadastrarVeiculo(View view) {

        String placa = "";
        String tipo_placa = "";
        String categoria = "";

        if (findViewById(R.id.layoutPlacaNormal).getVisibility() == View.VISIBLE) {

            Pattern pattern = Pattern.compile("[A-Z]{3}[0-9]{4}");
            placa = ((EditText)findViewById(R.id.editPlacaNormal)).getText().toString();
            placa.toUpperCase();

            if (!pattern.matcher(placa).matches()) {

                dialogHelper.showError("Verifique a placa.");

                return;
            }

            tipo_placa = "1";
        }
        else {

            Pattern pattern = Pattern.compile("[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}");
            placa = ((EditText)findViewById(R.id.editPlacaMercosul)).getText().toString();
            placa.toUpperCase();

            if (!pattern.matcher(placa).matches()) {

                dialogHelper.showError("Verifique a placa.");

                return;
            }

            tipo_placa = "2";
        }

        categoria = Integer.toString(((Spinner)findViewById(R.id.spinnerCategoria)).getSelectedItemPosition());

        if (categoria.equals("0")) {

            dialogHelper.showError("Selecione a categoria do veículo.");

            return;
        }

        final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewImage);

        if (vg.getChildCount() == 0) {

            dialogHelper.showError("Adicione pelo menos uma foto do veículo.");

            return;
        }

        final String descricao = ((EditText)findViewById(R.id.editTextRelato)).getText().toString();

        if (descricao.length() < 2) {

            dialogHelper.showError("Informe a descrição do veículo.");

            return;
        }

        final String placaFinal = placa;
        final String tipoFinal = tipo_placa;
        final String categoriaFinal = categoria;

        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {

                final List<SaspImage> imageList = new ArrayList<>();

                for (int i = 0; i < vg.getChildCount(); i++) {

                    Uri imgUri = (Uri)vg.getChildAt(i).findViewById(R.id.imageNew).getTag();

                    SaspImage sp = new SaspImage(VeiculosCadastrarVeiculoActivity.this);
                    sp.salvarImagem(imgUri);

                    imageList.add(sp);
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        dialogHelper.showProgress();

                        saspServer.abordagensCadastrarVeiculo(placaFinal, tipoFinal, categoriaFinal, imageList, descricao, new SaspResponse(VeiculosCadastrarVeiculoActivity.this) {

                            @Override
                            void onSaspResponse(String error, String msg, JSONObject extra) {

                                if (error.equals("1")) {

                                    dialogHelper.showError(msg);
                                }
                                else {

                                    for (int i = 0; i < imageList.size(); i++) {

                                        imageList.get(i).saveUploadObject(SaspImage.UPLOAD_OBJECT_MODULO_VEICULOS);
                                    }

                                    SaspServer.startServiceUploadImages(getApplicationContext());

                                    DataHolder.getInstance().setAdicionarVeiculoInfo(extra);

                                    setResult(1);
                                    finish();
                                }
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

                                for (int a = 0; a < imageList.size(); a++) {

                                    imageList.get(a).delete();
                                }

                                dialogHelper.dismissProgress();
                            }
                        });
                    }
                });
            }
        });
    }
}
