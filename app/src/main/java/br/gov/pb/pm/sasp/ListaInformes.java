package br.gov.pb.pm.sasp;

public class ListaInformes {

    public String id_informe;
    public String natureza;
    public String municipio;
    public String data_cadastro;

    public ListaInformes(String id_informe, String natureza, String municipio, String data_cadastro) {

        this.id_informe = id_informe;
        this.natureza = natureza;
        this.municipio = municipio;
        this.data_cadastro = data_cadastro;
    }
}
