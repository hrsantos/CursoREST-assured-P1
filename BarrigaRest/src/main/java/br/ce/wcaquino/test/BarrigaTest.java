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

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //anotation usada para configurar ordem de execução dos cenários 
public class BarrigaTest extends BaseTest{
	
	/*TESTANDO UMA APLICAÇÃO REAL*/
	
	// para evitar problemas na execução dos testes por nomes repetidos na criação da massa
	// utiliza-se a constante "CONTA_NAME" que receberá um nome dinâmico
	private static String CONTA_NAME = "Conta " + System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOV_ID;
	
	// Utiliza-se o @Before para executar alguma rotina específica da classe, no caso abaixo, o login
	// no caso do sistema SeuBarriga, para realizar alterações no sistema, é necessário extrair o token
	
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
		
		// implementa a validação do token como pré-requisito para a execução dos cenários de teste
		// tirando a necessidade de fazer essa validação em cada cenário de teste
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
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	// devido a grande quantidadede dados, para facilitar o envio dos dados no post
	// foi criada uma classe (Movimentação) com variáveis conrrespondentes aos campos para enviar 
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
			// verifica o tamanho da coleção que retorna 
			.body("$", hasSize(8))
			// verifica se as mensagens de campo obrigatório correspondem 
			.body("msg", hasItems("Data da Movimentação é obrigatório", "Data do pagamento é obrigatório",
					"Descrição é obrigatório", "Interessado é obrigatório", "Valor é obrigatório", 
					"Valor deve ser um número", "Conta é obrigatório", "Situação é obrigatório"))
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
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
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
			// na requisição, pois a conta está vinculada a movimentações, por isso foi utilizado
			// esses elementos referentes ao campo que está causando impedimento
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
	
	private Movimentacao gerarMovimentacaoValida() {
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setConta_id(CONTA_ID);
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
