package br.com._eacode.screemmatch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com._eacode.screemmatch.model.DadosSerie;
import br.com._eacode.screemmatch.service.ConsumoAPI;
import br.com._eacode.screemmatch.service.ConverteDados;

@SpringBootApplication
public class ScreemmatchApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(ScreemmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Aplicação iniciada com sucesso!");
		ConsumoAPI consumoAPI = new ConsumoAPI();
		String endereco = "http://www.omdbapi.com/?t=gilmore+girls&apikey=13f578a3";
		String json = consumoAPI.obterDados(endereco);
		System.out.println(json);

		ConverteDados converteDados = new ConverteDados();
		DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
		System.out.println(dados);
	}

}
