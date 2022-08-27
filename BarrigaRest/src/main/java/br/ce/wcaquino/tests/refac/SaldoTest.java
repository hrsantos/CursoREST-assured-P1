package br.ce.wcaquino.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.utils.BarrigaUtils;

public class SaldoTest extends BaseTest{
	
	/* Os testes abaixo utiliza um recurso de reset da massa, pré-configurado pelo Wagner Aquino, 
	 * simulando a estruturaçãoda massa de forma independente entre si, podendo se executar qualquer teste.
	 * */
	
	@Test
	public void deveCalcularSaldoContas() {
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para saldo");
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == " + CONTA_ID + "}.saldo", is("534.00"))
		;
	}
		
}
