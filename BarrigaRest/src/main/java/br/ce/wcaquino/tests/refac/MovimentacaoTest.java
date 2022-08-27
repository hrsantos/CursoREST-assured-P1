package br.ce.wcaquino.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;

public class MovimentacaoTest extends BaseTest{
	
	/* Os testes abaixo utiliza um recurso de reset da massa, pré-configurado pelo Wagner Aquino, 
	 * simulando a estruturaçãoda massa de forma independente entre si, podendo se executar qualquer teste.
	 * */

	@Test
	public void deveIncluirContaComSucesso() {	
		given()
			.body("{\"nome\":\"Conta inserida\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso(){
		Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");
		given()
			.body("{\"nome\":\"Conta alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is("Conta alterada"))
		;
	}
	
	@Test
	public void naoDeveIncluirContaComNomeRepetido() {
		given()
			.body("{\"nome\":\"Conta mesmo nome\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	// método para buscar id da conta utilizando o nome da conta como parâmetro
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
	
}
