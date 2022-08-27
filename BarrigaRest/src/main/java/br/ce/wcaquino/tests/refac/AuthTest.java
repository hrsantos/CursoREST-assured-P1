package br.ce.wcaquino.tests.refac;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest{
	
	/* Os testes abaixo utiliza um recurso de reset da massa, pr�-configurado pelo Wagner Aquino, 
	 * simulando a estrutura��oda massa de forma independente entre si, podendo se executar qualquer teste.
	 * */
	
	@Test
	public void naoDeveAcessarAPISemToken() {
		// nesse cen�rio estamos validando a n�o permiss�o de acesso sem o token, dessa forma,
		// � necess�rio especificar que n�o queremos utilizar a configura��o do @BeforeClass para 
		// validar o token, utilizando o m�todo "removeHeader" da classe "FilterableRequestSpecification"
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
