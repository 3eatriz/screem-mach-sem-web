package br.com._eacode.screemmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com._eacode.screemmatch.model.DadosEpisodio;
import br.com._eacode.screemmatch.model.DadosSerie;
import br.com._eacode.screemmatch.model.DadosTemporada;
import br.com._eacode.screemmatch.model.Episodio;
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
        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") +
                APIKEY);
        DadosSerie dadosSerie = converteDados.obterDados(json, DadosSerie.class);

        List<DadosTemporada> listaDeTemporadas = new ArrayList<>();

        for (int temporada = 1; temporada <= dadosSerie.totalTemporadas(); temporada++) {
            json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") +
                    "&season=" + temporada + APIKEY);
            DadosTemporada dadosTemporada = converteDados.obterDados(json,
                    DadosTemporada.class);
            listaDeTemporadas.add(dadosTemporada);
        }

        List<DadosEpisodio> dadosEpisodios = listaDeTemporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        // dadosEpisodios.stream()
        //         .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
        //         .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
        //         .limit(5).forEach(System.out::println);

        List<Episodio> episodio = listaDeTemporadas
                .stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        System.out.println("A partir de que ano você deseja ver os episódios? ");
        var ano = scanner.nextInt();
        scanner.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodio.stream()
        .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
        .forEach(e -> System.out.println(
                "Temporada: " + e.getTemporada() +
                " Episodio: " + e.getTitulo() +
                " Data lançamento: " +e.getDataLancamento().format(formatador)
        ));
    }
}
