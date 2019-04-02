package br.gov.pb.pm.sasp;

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


public class AbordagensFragmentMeusCadastrosActivityMeusCadastros extends Fragment {

    public static final String id = "FRAGMENT_ABORDAGENS_MEUS_CADASTROS";

    private RecyclerView recyclerView;
    private ArrayList<ListaAbordagens> listaAbordagens;
    private ListaAbordagensAdapter listaAbordagensAdapter;
    private SwipeRefreshLayout refreshLayout;

    private int index = 1;

    private boolean loaded_data = false;
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {

            view = inflater.inflate(R.layout.abordagens_fragment_meuscadastrosactivity_meuscadastros, container, false);
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

        refreshLayout = getActivity().findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AbordagensFragmentMeusCadastrosActivityMeusCadastros()).commitAllowingStateLoss();
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
        listaAbordagensAdapter = new ListaAbordagensAdapter(getActivity(), AbordagensMeusCadastrosActivity.dialogHelper, AbordagensMeusCadastrosActivity.saspServer, recyclerView, listaAbordagens);

        listaAbordagensAdapter.setOnLoadMoreListener(new ListaAbordagensAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {

                listaAbordagens.add(null);
                listaAbordagensAdapter.notifyItemInserted(listaAbordagens.size() - 1);

                AbordagensMeusCadastrosActivity.saspServer.abordagensMeusCadastros(index, new SaspResponse(getActivity()) {

                    @Override
                    void onSaspResponse(String error, String msg, JSONObject extra) {

                        int position = listaAbordagens.size();

                        try {

                            JSONArray jsonArray = extra.getJSONArray("Resultado");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);

                                listaAbordagens.add(new ListaAbordagens(json.getString("img_principal"), json.getString("img_busca"), json.getString("id_abordagem"), json.getString("numero_abordados"), json.getString("latitude"), json.getString("longitude"), json.getString("data_registro")));
                            }

                            listaAbordagensAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) {

                            if (AppUtils.DEBUG_MODE) Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                            return;
                        }

                        if (listaAbordagens.size() == 0) return;

                        listaAbordagens.remove(position - 1);
                        listaAbordagensAdapter.notifyItemRemoved(position);

                        index++;
                    }

                    @Override
                    void onResponse(String error) {

                        if (listaAbordagens.size() == 0) return;

                        listaAbordagens.remove(listaAbordagens.size() - 1);
                        listaAbordagensAdapter.notifyItemRemoved(listaAbordagens.size());
                    }

                    @Override
                    void onNoResponse(String error) {

                        if (listaAbordagens.size() == 0) return;

                        listaAbordagens.remove(listaAbordagens.size() - 1);
                        listaAbordagensAdapter.notifyItemRemoved(listaAbordagens.size());
                    }

                    @Override
                    void onPostResponse() {

                        listaAbordagensAdapter.setLoaded();
                    }
                });
            }
        });

        recyclerView.setAdapter(listaAbordagensAdapter);

        AbordagensMeusCadastrosActivity.saspServer.abordagensMeusCadastros(index, new SaspResponse(getActivity()) {

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
}
