package br.gov.pb.pm.sasp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentMainActivityResultadoBuscaPessoa extends Fragment {

    public static final String id = "FRAGMENT_PESSOAS_RESULTADO_BUSCA";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_mainactivity_resultado_busca_pessoas, container, false);
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
    }
}
