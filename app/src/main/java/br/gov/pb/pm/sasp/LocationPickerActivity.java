package br.gov.pb.pm.sasp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;

public class LocationPickerActivity extends SaspActivity implements GoogleMap.OnMapLoadedCallback {

    public static final int LOCATION_PICKER_INTENT = 211;
    
    private DialogHelper dialogHelper;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private SimpleLocation simpleLocation;
    private boolean mapLoaded;
    private Marker marker;
    private View layoutConfirmarLocal;
    private boolean locationPicked = false;
    private boolean buscarAbordagem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        dialogHelper = new DialogHelper(LocationPickerActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        layoutConfirmarLocal = findViewById(R.id.layoutConfirmarLocal);

        buscarAbordagem = getIntent().hasExtra("buscarAbordagem");

        simpleLocation = new SimpleLocation(LocationPickerActivity.this);

        if (!simpleLocation.hasLocationEnabled()) {

            dialogHelper.showError("Habilite o GPS do seu aparelho para melhorar a precisão das coordenadas.");
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(final GoogleMap googleMap) {

                map = googleMap;
                mapLoaded = false;

                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(simpleLocation.getLatitude(), simpleLocation.getLongitude()), 12f);
                googleMap.moveCamera(center);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng latLng) {

                        Snackbar.make(findViewById(android.R.id.content), "Pressione e segure para marcar o local.", 1000).show();
                    }
                });

                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(LatLng latLng) {

                        if (map.getCameraPosition().zoom < 12f) {

                            dialogHelper.showError("Aumente o zoom para selecionar o local com uma maior precisão.");

                            return;
                        }

                        if (marker != null) {

                            marker.setPosition(latLng);
                        }
                        else {

                            marker = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }

                        locationPicked = true;

                        layoutConfirmarLocal.setAlpha(0f);
                        layoutConfirmarLocal.setVisibility(View.VISIBLE);
                        layoutConfirmarLocal.animate().alpha(1.0f);
                    }
                });
            }
        });
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        dialogHelper.dismissProgress();

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            dialogHelper.dismissProgress();

            if (resultCode == RESULT_OK) {

            }
        }
        else if (requestCode == 100) {

            dialogHelper.showError("Result: " + resultCode);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void fecharJanela(View view) {

        finish();
    }

    public void novaAbordagemSnack(View view) {

        Snackbar.make(findViewById(android.R.id.content), "Pressione e segure para excluir a abordagem.", 1000).show();
    }

    public void buttonAdicionarImagem(View view) {

    }

    public void buttonConfirmarLocal(View view) {

        if (!locationPicked) {

            dialogHelper.showError("Escolha um local no mapa.");

            return;
        }

        layoutConfirmarLocal.setVisibility(View.GONE);

        mapLoaded = false;

        map.setOnMapLoadedCallback(this);

        DataHolder.getInstance().setCadastrarAbordagemLatitude(Double.toString(marker.getPosition().latitude));
        DataHolder.getInstance().setCadastrarAbordagemLongitude(Double.toString(marker.getPosition().longitude));

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14f);
        map.moveCamera(center);
        //marker.remove();

        dialogHelper.showProgress();

        final long startTime = System.currentTimeMillis();
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (buscarAbordagem) {

                    map.snapshot(new GoogleMap.SnapshotReadyCallback() {

                        @Override
                        public void onSnapshotReady(Bitmap bmp) {

                            saveLocationImage(bmp);
                        }
                    });
                }
                else {

                    if (mapLoaded) {

                        map.snapshot(new GoogleMap.SnapshotReadyCallback() {

                            @Override
                            public void onSnapshotReady(Bitmap bmp) {

                                saveLocationImage(bmp);
                            }
                        });
                    }
                    else {

                        if (System.currentTimeMillis() - startTime < 20000L) {

                            handler.postDelayed(this, 1000);
                        }
                        else {

                            map.snapshot(new GoogleMap.SnapshotReadyCallback() {

                                @Override
                                public void onSnapshotReady(Bitmap bmp) {

                                    saveLocationImage(bmp);
                                }
                            });
                        }
                    }
                }
            }
        }, 1000);
    }

    private void saveLocationImage(Bitmap bmp) {

        try {

            File imgLocal = File.createTempFile("imgLocal", ".jpg", getCacheDir());

            FileOutputStream stream = new FileOutputStream(imgLocal);

            int dimension = Math.min(bmp.getWidth(), bmp.getHeight());

            Bitmap bitmapBusca = ThumbnailUtils.extractThumbnail(bmp, dimension, dimension);
            bitmapBusca.compress(Bitmap.CompressFormat.JPEG, 80, stream);

            stream.close();

            setResult(1, getIntent().putExtra("imgLocal", imgLocal));
        }
        catch (Exception e) { }

        finish();
    }

    @Override
    public void onMapLoaded() {

        mapLoaded = true;
    }
}
