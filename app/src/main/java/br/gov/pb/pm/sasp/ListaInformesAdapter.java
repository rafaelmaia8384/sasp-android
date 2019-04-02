package br.gov.pb.pm.sasp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class ListaInformesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private DialogHelper dialogHelper;
    private SaspServer saspServer;
    private ArrayList<ListaInformes> listaInformes;

    private RecyclerView rv;

    private final int VIEW_ITEM = 0;
    private final int VIEW_LOADING = 1;

    private boolean isLoading;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    private OnLoadMoreListener onLoadMoreListener;

    public ListaInformesAdapter(Context context, DialogHelper dialogHelper, SaspServer saspServer, RecyclerView rv, ArrayList<ListaInformes> listaInformes) {

        this.context = context;
        this.dialogHelper = dialogHelper;
        this.saspServer = saspServer;
        this.rv = rv;
        this.listaInformes = listaInformes;

        final LinearLayoutManager llm = (LinearLayoutManager) rv.getLayoutManager();

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = llm.getItemCount();
                lastVisibleItem = llm.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                    if (onLoadMoreListener != null) {

                        onLoadMoreListener.onLoadMore();
                    }

                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {

        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoading = false;
            }
        }, 300);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        if (viewType == VIEW_ITEM) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lista_informes, parent, false);

            final RecyclerView rv = parent.findViewById(R.id.recyclerView);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

//                    int pos = rv.getChildAdapterPosition(view);
//
//                    dialogHelper.showProgress();
//
//                    saspServer.informesPerfil(listaInformes.get(pos).id_informe, new SaspResponse(context) {
//
//                        @Override
//                        void onSaspResponse(String error, String msg, JSONObject extra) {
//
//                            DataHolder.getInstance().setInformeData(extra);
//
//                            Intent i = new Intent(context, AbordagensPerfilAbordagemActivity.class);
//                            ((SaspActivity)context).startActivityForResult(i, 400);
//                        }
//
//                        @Override
//                        void onResponse(String error) {
//
//                            dialogHelper.showError(error);
//                        }
//
//                        @Override
//                        void onNoResponse(String error) {
//
//                            dialogHelper.showError(error);
//                        }
//
//                        @Override
//                        void onPostResponse() {
//
//                            dialogHelper.dismissProgress();
//                        }
//                    });
                }
            });

            return new mViewHolder(view);
        }
        else { //VIEW_LOADING

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading, parent, false);

            return new mLoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof mViewHolder) {

            ListaInformes listaInforme = listaInformes.get(position);

            ((mViewHolder)holder).natureza.setText(listaInforme.natureza);
            ((mViewHolder)holder).municipio.setText(listaInforme.municipio);
            ((mViewHolder)holder).dataCadastro.setText(AppUtils.formatarData(listaInforme.data_cadastro));
        }
    }

    @Override
    public int getItemCount() {

        return listaInformes.size();
    }

    @Override
    public int getItemViewType(int position) {

        return listaInformes.get(position) == null ? VIEW_LOADING : VIEW_ITEM;
    }

    public class mViewHolder extends RecyclerView.ViewHolder {

        TextView natureza;
        TextView municipio;
        TextView dataCadastro;

        public mViewHolder(View view) {

            super(view);

            natureza = view.findViewById(R.id.textNatureza);
            municipio = view.findViewById(R.id.textMunicipio);
            dataCadastro = view.findViewById(R.id.dataCadastro);
        }
    }

    public class mLoadingViewHolder extends RecyclerView.ViewHolder {

        public mLoadingViewHolder(View view) {

            super(view);
        }
    }

    public interface OnLoadMoreListener {

        void onLoadMore();
    }
}
