package br.ce.wcaquino.tests.refact.suite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.tests.refac.AuthTest;
import br.ce.wcaquino.tests.refac.ContasTest;
import br.ce.wcaquino.tests.refac.MovimentacaoTest;
import br.ce.wcaquino.tests.refac.SaldoTest;
import io.restassured.RestAssured;

@RunWith(Suite.class)
@SuiteClasses({
	ContasTest.class,
	MovimentacaoTest.class,
	SaldoTest.class,
	AuthTest.class})

public class SuiteDeTeste extends BaseTest{

	// Utiliza-se o @Before para executar alguma rotina espec�fica da classe, no caso abaixo, o login
		// no caso do sistema SeuBarriga, para realizar altera��es no sistema, � necess�rio extrair o token
			
		@BeforeClass
		public static void logar() {
			
			Map<String, String> login = new HashMap<String, String>();
			login.put("email", "tester@testador.com");
			login.put("senha", "123");

			String TOKEN = given().body(login).when().post("/signin").then().statusCode(200).extract().path("token");

			// implementa a valida��o do token como pr�-requisito para a execu��o dos
			// cen�rios de teste
			// tirando a necessidade de fazer essa valida��o em cada cen�rio de teste
			RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
			
			RestAssured.get("/reset").then().statusCode(200);
		}
}
