package br.ce.wcaquino.utils;

import io.restassured.RestAssured;

public class BarrigaUtils {

	// método para buscar id da conta utilizando o nome da conta como parâmetro
	public static Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
		
	// método para buscar id da conta utilizando a descrição da movimentação da
	// conta como parâmetro
	public static Integer getIdContaPelaDescricao(String desc) {
		return RestAssured.get("/transacoes?descricao=" + desc).then().extract().path("id[0]");
	}
}
