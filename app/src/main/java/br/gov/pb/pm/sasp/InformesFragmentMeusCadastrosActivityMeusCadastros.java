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


public class InformesFragmentMeusCadastrosActivityMeusCadastros extends Fragment {

    public static final String id = "FRAGMENT_ABORDAGENS_MEUS_CADASTROS";

    private RecyclerView recyclerView;
    private ArrayList<ListaInformes> listaInformes;
    private ListaInformesAdapter listaInformesAdapter;
    private SwipeRefreshLayout refreshLayout;

    String[] natureza;

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

        natureza = getActivity().getResources().getStringArray(R.array.informe_natureza);

        refreshLayout = getActivity().findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new InformesFragmentMeusCadastrosActivityMeusCadastros()).commitAllowingStateLoss();
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

        listaInformes = new ArrayList<>();
        listaInformesAdapter = new ListaInformesAdapter(getActivity(), InformesMeusCadastrosActivity.dialogHelper, InformesMeusCadastrosActivity.saspServer, recyclerView, listaInformes);

        listaInformesAdapter.setOnLoadMoreListener(new ListaInformesAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {

                listaInformes.add(null);
                listaInformesAdapter.notifyItemInserted(listaInformes.size() - 1);

                InformesMeusCadastrosActivity.saspServer.informesMeusCadastros(index, DataHolder.getInstance().getInformesSenha(), new SaspResponse(getActivity()) {

                    @Override
                    void onSaspResponse(String error, String msg, JSONObject extra) {

                        int position = listaInformes.size();

                        try {

                            JSONArray jsonArray = extra.getJSONArray("Resultado");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);

                                listaInformes.add(new ListaInformes(json.getString("id_informe"), natureza[json.getInt("natureza")], json.getString("municipio"), json.getString("data_registro")));
                            }

                            listaInformesAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) {

                            if (AppUtils.DEBUG_MODE) Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                            return;
                        }

                        if (listaInformes.size() == 0) return;

                        listaInformes.remove(position - 1);
                        listaInformesAdapter.notifyItemRemoved(position);

                        index++;
                    }

                    @Override
                    void onResponse(String error) {

                        if (listaInformes.size() == 0) return;

                        listaInformes.remove(listaInformes.size() - 1);
                        listaInformesAdapter.notifyItemRemoved(listaInformes.size());
                    }

                    @Override
                    void onNoResponse(String error) {

                        if (listaInformes.size() == 0) return;

                        listaInformes.remove(listaInformes.size() - 1);
                        listaInformesAdapter.notifyItemRemoved(listaInformes.size());
                    }

                    @Override
                    void onPostResponse() {

                        listaInformesAdapter.setLoaded();
                    }
                });
            }
        });

        recyclerView.setAdapter(listaInformesAdapter);

        InformesMeusCadastrosActivity.saspServer.informesMeusCadastros(index, DataHolder.getInstance().getInformesSenha(), new SaspResponse(getActivity()) {

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

                        listaInformes.add(new ListaInformes(json.getString("id_informe"), natureza[json.getInt("natureza")], json.getString("municipio"), json.getString("data_registro")));
                    }

                    listaInformesAdapter.notifyDataSetChanged();
                    listaInformesAdapter.setLoaded();

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
