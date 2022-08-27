package br.ce.wcaquino.utils;

import io.restassured.RestAssured;

public class BarrigaUtils {

	// m�todo para buscar id da conta utilizando o nome da conta como par�metro
	public static Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
		
	// m�todo para buscar id da conta utilizando a descri��o da movimenta��o da
	// conta como par�metro
	public static Integer getIdContaPelaDescricao(String desc) {
		return RestAssured.get("/transacoes?descricao=" + desc).then().extract().path("id[0]");
	}
}
