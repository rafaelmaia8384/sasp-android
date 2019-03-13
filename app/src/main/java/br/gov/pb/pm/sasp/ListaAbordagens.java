package br.gov.pb.pm.sasp;

public class ListaAbordagens {

    public String img_principal;
    public String img_busca;
    public String id_abordagem;
    public String numero_abordados;
    public String latitude;
    public String longitude;
    public String data_cadastro;

    public ListaAbordagens(String img_principal, String img_busca, String id_abordagem, String numero_abordados, String latitude, String longitude, String data_cadastro) {

        this.img_principal = img_principal;
        this.img_busca = img_busca;
        this.id_abordagem = id_abordagem;
        this.numero_abordados = numero_abordados;
        this.latitude = latitude;
        this.longitude = longitude;
        this.data_cadastro = data_cadastro;
    }
}
