package br.ce.wcaquino.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //anotation usada para configurar ordem de execu��o dos cen�rios 
public class BarrigaTest extends BaseTest{
	
	/*TESTANDO UMA APLICA��O REAL*/
	
	// para evitar problemas na execu��o dos testes por nomes repetidos na cria��o da massa
	// utiliza-se a constante "CONTA_NAME" que receber� um nome din�mico
	private static String CONTA_NAME = "Conta " + System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOV_ID;
	
	// Utiliza-se o @Before para executar alguma rotina espec�fica da classe, no caso abaixo, o login
	// no caso do sistema SeuBarriga, para realizar altera��es no sistema, � necess�rio extrair o token
	
	@BeforeClass
	public static void logar() {
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "tester@testador.com");
		login.put("senha", "123");
		
		String TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
		
		// implementa a valida��o do token como pr�-requisito para a execu��o dos cen�rios de teste
		// tirando a necessidade de fazer essa valida��o em cada cen�rio de teste
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
	}

	@Test
	public void t02_deveIncluirContaComSucesso() {	
		CONTA_ID= given()
			//.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\":\"" + CONTA_NAME + "\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t03_deveAlterarContaComSucesso(){
		given()
			//.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\":\"" + CONTA_NAME + "Alterada com Sucesso\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is(CONTA_NAME + "Alterada com Sucesso"))
		;
	}
	
	@Test
	public void t04_naoDeveIncluirContaComNomeRepetido() {
		given()
			//.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\":\"" + CONTA_NAME + "Alterada com Sucesso\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("J� existe uma conta com esse nome!"))
		;
	}
	
	// devido a grande quantidadede dados, para facilitar o envio dos dados no post
	// foi criada uma classe (Movimenta��o) com vari�veis conrrespondentes aos campos para enviar 
	// no .body somente o objeto 
	@Test
	public void t05_deveIncluirMovimentacaoComSucesso() {
		Movimentacao movimentacao = gerarMovimentacaoValida();
		
		MOV_ID = given()
		    //.header("Authorization", "JWT " + TOKEN)
		    .body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t06_deveValidarCamposObritorios() {
		given()
		    //.header("Authorization", "JWT " + TOKEN)
		    .body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			// verifica o tamanho da cole��o que retorna 
			.body("$", hasSize(8))
			// verifica se as mensagens de campo obrigat�rio correspondem 
			.body("msg", hasItems("Data da Movimenta��o � obrigat�rio", "Data do pagamento � obrigat�rio",
					"Descri��o � obrigat�rio", "Interessado � obrigat�rio", "Valor � obrigat�rio", 
					"Valor deve ser um n�mero", "Conta � obrigat�rio", "Situa��o � obrigat�rio"))
		;
	}
	
	@Test
	public void t07_naoDeveCadastrarMovimentacaoFutura() {
		Movimentacao movimentacao = gerarMovimentacaoValida();
		movimentacao.setData_transacao(DataUtils.getDataDiferencaDias(2));
		
		given()
		    //.header("Authorization", "JWT " + TOKEN)
		    .body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("msg", hasItem("Data da Movimenta��o deve ser menor ou igual � data atual"))
		;
	}
	
	@Test
	public void t08_naoDeveRemoverContaComMovimentacao() {
		given()
			//.header("Authorization", "JWT " + TOKEN)
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			// no caso aqui, na tentativa de deletar,o sistema retornou um problema de integridade
			// na requisi��o, pois a conta est� vinculada a movimenta��es, por isso foi utilizado
			// esses elementos referentes ao campo que est� causando impedimento
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void t09_deveCalcularSaldoContas() {
		given()
			//.header("Authorization", "JWT " + TOKEN)
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == " + CONTA_ID + "}.saldo", is("100.00"))
		;
	}
	
	@Test
	public void t10_deveRemoverMovimentacao() {
		given()
			//.header("Authorization", "JWT " + TOKEN)
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	@Test
	public void t11_naoDeveAcessarAPISemToken() {
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
	
	private Movimentacao gerarMovimentacaoValida() {
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setConta_id(CONTA_ID);
		movimentacao.setDescricao("teste de cria��o de movimenta��o");
		movimentacao.setEnvolvido("Thomas Anderson");
		movimentacao.setTipo("REC");
		movimentacao.setData_transacao(DataUtils.getDataDiferencaDias(-1));
		movimentacao.setData_pagamento(DataUtils.getDataDiferencaDias(5));
		movimentacao.setValor(100f);
		movimentacao.setStatus(true);
		return movimentacao;
	}
	
}
