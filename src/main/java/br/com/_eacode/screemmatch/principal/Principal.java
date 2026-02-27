package br.com._eacode.screemmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

                List<Episodio> episodio = listaDeTemporadas
                                .stream()
                                .flatMap(t -> t.episodios().stream()
                                                .map(d -> new Episodio(t.numero(), d)))
                                .collect(Collectors.toList());

                episodio.forEach(System.out::println);

                // System.out.println("Digite um trecho do título do episódio que deseja
                // buscar:");
                // var trechoTitulo = scanner.nextLine();
                // Optional<Episodio> episodioEncontrado = episodio.stream()
                // .filter(e ->
                // e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                // .findFirst();

                // if (episodioEncontrado.isPresent()) {
                // System.out.println("Episódio encontrado: " + episodioEncontrado.get());
                // } else {
                // System.out.println("Nenhum episódio encontrado com o trecho do título
                // fornecido.");
                // }

                // System.out.println("Top 10 Episodios");
                // dadosEpisodios.stream()
                // .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                // .peek(e -> System.out.println("Primeiro filtro(N/A) " + e))
                // .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                // .peek(e -> System.out.println("Ordenação " + e))
                // .limit(10)
                // .peek(e -> System.out.println("Limite " + e))
                // .map(e -> e.titulo().toUpperCase())
                // .peek(e -> System.out.println("Mapeamento " + e))
                // .forEach(System.out::println);

                Map<Integer, Double> avaliacoesPorTemporada = episodio.stream()
                                .filter(e -> e.getAvaliacao() > 0.0)
                                .collect(Collectors.groupingBy(Episodio::getTemporada,
                                                Collectors.averagingDouble(Episodio::getAvaliacao)));

                System.out.println(avaliacoesPorTemporada);

                DoubleSummaryStatistics est = episodio.stream()
                                .filter(e -> e.getAvaliacao() > 0.0)
                                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
                System.out.printf("Média: %.1f%n", est.getAverage());
                System.out.printf("Melhor episódio: %.1f%n", est.getMax());
                System.out.printf("Pior episódio: %.1f%n", est.getMin());
                System.out.printf("Quantidade: %d%n", est.getCount());
        }
}
