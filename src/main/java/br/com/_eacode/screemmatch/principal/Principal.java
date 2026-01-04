package br.com._eacode.screemmatch.principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com._eacode.screemmatch.model.DadosEpisodio;
import br.com._eacode.screemmatch.model.DadosSerie;
import br.com._eacode.screemmatch.model.DadosTemporada;
import br.com._eacode.screemmatch.service.ConsumoAPI;
import br.com._eacode.screemmatch.service.ConverteDados;

public class Principal {

    private Scanner scanner = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados converteDados = new ConverteDados();

    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=13f578a3";

    public void exibeMenu() {
        System.out.println("Digite o nome da série que deseja buscar:");
        var nomeSerie = scanner.nextLine();
        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + APIKEY);
        DadosSerie dadosSerie = converteDados.obterDados(json, DadosSerie.class);
        // System.out.println(dadosSerie);
        		
		List<DadosTemporada> listaDeTemporadas = new ArrayList<>();

		for (int temporada = 1; temporada <= dadosSerie.totalTemporadas(); temporada++) {
			json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + temporada + APIKEY);
            // System.out.println(json);
			DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
			listaDeTemporadas.add(dadosTemporada);
		}

		// listaDeTemporadas.forEach(System.out::println);

        // for (int i = 0; i < dadosSerie.totalTemporadas(); i++) {
        //     List<DadosEpisodio> episodios = listaDeTemporadas.get(i).episodios();
        //     for (int j = 0; j < episodios.size(); j++) {
        //         System.out.println(episodios.get(j).titulo());
        //     }
        // }

        listaDeTemporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }
}
