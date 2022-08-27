package br.ce.wcaquino.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.test.Movimentacao;
import br.ce.wcaquino.utils.BarrigaUtils;
import br.ce.wcaquino.utils.DataUtils;

public class ContasTest extends BaseTest{
	
	/* Os testes abaixo utiliza um recurso de reset da massa, pré-configurado pelo Wagner Aquino, 
	 * simulando a estruturaçãoda massa de forma independente entre si, podendo se executar qualquer teste.
	 * */
	
	@Test
	public void deveIncluirMovimentacaoComSucesso() {
		Movimentacao movimentacao = gerarMovimentacaoValida();
		given()
		    .body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void deveValidarCamposObritorios() {
		given()
		    .body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			// verifica o tamanho da coleção que retorna 
			.body("$", hasSize(8))
			// verifica se as mensagens de campo obrigatório correspondem 
			.body("msg", hasItems("Data da Movimentação é obrigatório", "Data do pagamento é obrigatório",
					"Descrição é obrigatório", "Interessado é obrigatório", "Valor é obrigatório", 
					"Valor deve ser um número", "Conta é obrigatório", "Situação é obrigatório"))
		;
	}
	
	@Test
	public void naoDeveCadastrarMovimentacaoFutura() {
		Movimentacao movimentacao = gerarMovimentacaoValida();
		movimentacao.setData_transacao(DataUtils.getDataDiferencaDias(2));
		
		given()
		    .body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}
	
	@Test
	public void naoDeveRemoverContaComMovimentacao() {
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta com movimentacao");
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			// no caso aqui, na tentativa de deletar,o sistema retornou um problema de integridade
			// na requisição, pois a conta está vinculada a movimentações, por isso foi utilizado
			// esses elementos referentes ao campo que está causando impedimento
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void deveRemoverMovimentacao() {
		Integer MOV_ID = BarrigaUtils.getIdContaPelaDescricao("Movimentacao para exclusao");
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	
	private Movimentacao gerarMovimentacaoValida() {
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
		movimentacao.setDescricao("teste de criação de movimentação");
		movimentacao.setEnvolvido("Thomas Anderson");
		movimentacao.setTipo("REC");
		movimentacao.setData_transacao(DataUtils.getDataDiferencaDias(-1));
		movimentacao.setData_pagamento(DataUtils.getDataDiferencaDias(5));
		movimentacao.setValor(100f);
		movimentacao.setStatus(true);
		return movimentacao;
	}
	
}
