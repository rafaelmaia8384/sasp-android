package br.gov.pb.pm.sasp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AbordagensFragmentMainActivityResultadoBusca extends Fragment {

    public static final String id = "FRAGMENT_ABORDAGENS_RESULTADO_BUSCA";

    private SupportMapFragment mapFragment;
    private boolean loaded_data = false;
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {

            view = inflater.inflate(R.layout.abordagens_fragment_mainactivity_resultado_busca, container, false);
        }

        return view;
    }

    @Override
    public void onDestroyView() {

        if (view != null) {

            try {

                ((ViewGroup) view.getParent()).removeAllViews();
            }
            catch (Exception e) { }
        }

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.buttonVoltar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();
            }
        });

        if (!loaded_data) {

            mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);

            processData();

            loaded_data = true;
        }
    }

    private void processData() {

        MainActivity.saspServer.abordagensBuscarAbordagem(new SaspResponse(getActivity()) {

            @Override
            void onSaspResponse(String error, String msg, final JSONObject extra) {

                if (!isVisible()) return;

                if (error.equals("1")) {

                    getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                    ((TextView)getActivity().findViewById(R.id.textError)).setText(msg);

                    return;
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

                        String latitude = DataHolder.getInstance().getCadastroAbordagemLatitude();
                        String longitude = DataHolder.getInstance().getCadastroAbordagemLongitude();

                        if (latitude != null && longitude != null) {

                            double lat = Double.parseDouble(DataHolder.getInstance().getCadastroAbordagemLatitude());
                            double lon = Double.parseDouble(DataHolder.getInstance().getCadastroAbordagemLongitude());

                            LatLng location = new LatLng(lat, lon);

                            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(location, 12f);
                            googleMap.moveCamera(center);

                            googleMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title("Ponto da busca")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }

                        try {

                            JSONArray jsonArray = extra.getJSONArray("Resultado");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                double markerLat = jsonObject.getDouble("latitude");
                                double markerLon = jsonObject.getDouble("longitude");

                                Marker m = googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(markerLat, markerLon))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                m.setTag(jsonObject);

                                if (i == 0 && latitude == null & longitude == null) {

                                    LatLng location = new LatLng(markerLat, markerLon);

                                    CameraUpdate center = CameraUpdateFactory.newLatLngZoom(location, 12f);
                                    googleMap.moveCamera(center);
                                }
                            }
                        }
                        catch (Exception e) {

                            if (AppUtils.DEBUG_MODE) Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }

                        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            public View getInfoWindow(Marker marker) {

                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {

                                if (marker.getTag() != null) {

                                    LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = layoutInflater.inflate(R.layout.layout_lista_abordagens_busca, null);

                                    final JSONObject j = (JSONObject)marker.getTag();

                                    try {

                                        ((TextView)view.findViewById(R.id.textNumeroAbordados)).setText(j.getString("numero_abordados"));
                                        ((TextView)view.findViewById(R.id.textGPS)).setText(j.getString("latitude") + ", " + j.getString("longitude"));
                                        ((TextView)view.findViewById(R.id.dataCadastro)).setText(AppUtils.formatarDataSimple(j.getString("data_registro")));

                                        ImageLoader.getInstance().displayImage(SaspServer.getImageAddress(j.getString("img_busca"), SaspImage.UPLOAD_OBJECT_MODULO_ABORDAGENS, true), (ImageView)view.findViewById(R.id.imagemPerfil));
                                    }
                                    catch (Exception e) {

                                        if (AppUtils.DEBUG_MODE) Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }

                                    return view;
                                }

                                return null;
                            }
                        });

                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                if (marker.getTag() != null) {

                                    marker.showInfoWindow();
                                }

                                return false;
                            }
                        });

                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                            @Override
                            public void onInfoWindowClick(Marker marker) {

                                if (marker.getTag() != null) {

                                    JSONObject j = (JSONObject)marker.getTag();

                                    try {

                                        String id_abordagem = j.getString("id_abordagem");

                                        MainActivity.dialogHelper.showProgress();

                                        MainActivity.saspServer.abordagensPerfil(id_abordagem, new SaspResponse(getActivity()) {

                                            @Override
                                            void onSaspResponse(String error, String msg, JSONObject extra) {

                                                if (error.equals("0")) {

                                                    DataHolder.getInstance().setAbordagemData(extra);
                                                    Intent i = new Intent(getActivity(), AbordagensPerfilAbordagemActivity.class);
                                                    startActivity(i);
                                                }
                                                else {

                                                    MainActivity.dialogHelper.showError(msg);
                                                }
                                            }

                                            @Override
                                            void onResponse(String error) {

                                            }

                                            @Override
                                            void onNoResponse(String error) {

                                            }

                                            @Override
                                            void onPostResponse() {

                                                MainActivity.dialogHelper.dismissProgress();
                                            }
                                        });
                                    }
                                    catch (Exception e) {

                                        if (AppUtils.DEBUG_MODE) Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }
                });

                getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
                getActivity().findViewById(R.id.layoutMap).setVisibility(View.VISIBLE);
            }

            @Override
            void onResponse(String error) {

                if (!isVisible()) return;

                getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.textError)).setText("Erro de conexão com o servidor.");
            }

            @Override
            void onNoResponse(String error) {

                if (!isVisible()) return;

                getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.textError)).setText("Erro de conexão com o servidor.");
            }

            @Override
            void onPostResponse() {

                DataHolder.getInstance().setMapaLatitude(null);
                DataHolder.getInstance().setMapaLongitude(null);

                getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
            }
        });
    }
}
