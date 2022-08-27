package br.ce.wcaquino.rest.core;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;

public class BaseTest implements Constantes {

	// configuração de rotina que será executada antes da execução dos testes
	@BeforeClass
	public static void setup() {
		//Parâmetros fixos
		RestAssured.baseURI = APP_BASE_URL;
		RestAssured.basePath = APP_BASE_PATH;
		RestAssured.port = APP_PORT;
		
		// Parametrização básica das requisições
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.setContentType(APP_CONTENT_TYPE); //tipo de doc (Json)
		RestAssured.requestSpecification = reqBuilder.build();
		
		// Parametrização básica das respostas
		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectResponseTime(Matchers.lessThan(MAX_TIMEOUT)); //tempo máximo para responder
		RestAssured.responseSpecification = resBuilder.build();
		
		// esse método configura o a geração de log somente em caso de falha do teste
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	} 
}
