package br.ce.wcaquino.tests.refac;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest{
	
	/* Os testes abaixo utiliza um recurso de reset da massa, pré-configurado pelo Wagner Aquino, 
	 * simulando a estruturaçãoda massa de forma independente entre si, podendo se executar qualquer teste.
	 * */
	
	@Test
	public void naoDeveAcessarAPISemToken() {
		// nesse cenário estamos validando a não permissão de acesso sem o token, dessa forma,
		// é necessário especificar que não queremos utilizar a configuração do @BeforeClass para 
		// validar o token, utilizando o método "removeHeader" da classe "FilterableRequestSpecification"
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
}
