package br.gov.pb.pm.sasp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AbordagensFragmentMainActivityAbordagens extends Fragment {

    public static final String id = "FRAGMENT_ABORDAGENS";

    private RecyclerView recyclerView;
    private ArrayList<ListaAbordagens> listaAbordagens;
    private ListaAbordagensAdapter listaAbordagensAdapter;
    private SwipeRefreshLayout refreshLayout;

    private String date_time = "9999-01-01 00:00:00";

    private int index = 1;

    private boolean loaded_data = false;
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {

            view = inflater.inflate(R.layout.abordagens_fragment_mainactivity_abordagens, container, false);
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

        getActivity().findViewById(R.id.buttonBuscar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {


                    }
                });
            }
        });

        getActivity().findViewById(R.id.buttonCadastrar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Intent i = new Intent(getActivity(), AbordagensCadastrarAbordagemActivity.class);
                        getActivity().startActivityForResult(i, AbordagensCadastrarAbordagemActivity.CODE_ACTIVITY_CADASTRAR_ABORDAGEM);
                    }
                });
            }
        });

        getActivity().findViewById(R.id.buttonMeusCadastros).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                    }
                });
            }
        });

        refreshLayout = getActivity().findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AbordagensFragmentMainActivityAbordagens()).commitAllowingStateLoss();
            }
        });

        if (!loaded_data) {

            processData();

            loaded_data = true;
        }
    }

    private void processData() {

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        recyclerView = getActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(llm);

        listaAbordagens = new ArrayList<>();
        listaAbordagensAdapter = new ListaAbordagensAdapter(getActivity(), MainActivity.dialogHelper, MainActivity.saspServer, recyclerView, listaAbordagens);

        listaAbordagensAdapter.setOnLoadMoreListener(new ListaAbordagensAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {


            }
        });

        recyclerView.setAdapter(listaAbordagensAdapter);

        MainActivity.saspServer.saspServerDateTime(new SaspResponse(getActivity()) {

            @Override
            void onSaspResponse(String error, String msg, JSONObject extra) {

                if (!isVisible()) return;

                try {

                    date_time = extra.getString("date_time");
                }
                catch (Exception e) { }

                MainActivity.saspServer.abordagensUltimosCadastros(index, date_time, new SaspResponse(getActivity()) {

                    @Override
                    void onSaspResponse(String error, String msg, JSONObject extra) {

                        if (!isVisible()) return;

                        if (error.equals("1")) {

                            getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                            ((TextView)getActivity().findViewById(R.id.textError)).setText(msg);

                            return;
                        }

                        try {

                            JSONArray jsonArray = extra.getJSONArray("Resultado");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);

                                listaAbordagens.add(new ListaAbordagens(json.getString("img_principal"), json.getString("img_busca"), json.getString("id_abordagem"), json.getString("numero_abordados"), json.getString("latitude"), json.getString("longitude"), json.getString("data_registro")));
                            }

                            listaAbordagensAdapter.notifyDataSetChanged();
                            listaAbordagensAdapter.setLoaded();

                            getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        catch (JSONException e) {

                            if (AppUtils.DEBUG_MODE) Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                            return;
                        }

                        index++;
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

                        if (!isVisible()) return;

                        getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            void onResponse(String error) {

                if (!isVisible()) return;

                getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.textError)).setText("Erro de conexão com o servidor.");
                getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
            }

            @Override
            void onNoResponse(String error) {

                if (!isVisible()) return;

                getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.textError)).setText("Erro de conexão com o servidor.");
                getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
            }

            @Override
            void onPostResponse() {

            }
        });
    }
}
