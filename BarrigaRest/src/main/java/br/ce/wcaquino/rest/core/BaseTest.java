package br.ce.wcaquino.rest.core;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;

public class BaseTest implements Constantes {

	// configura��o de rotina que ser� executada antes da execu��o dos testes
	@BeforeClass
	public static void setup() {
		//Par�metros fixos
		RestAssured.baseURI = APP_BASE_URL;
		RestAssured.basePath = APP_BASE_PATH;
		RestAssured.port = APP_PORT;
		
		// Parametriza��o b�sica das requisi��es
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.setContentType(APP_CONTENT_TYPE); //tipo de doc (Json)
		RestAssured.requestSpecification = reqBuilder.build();
		
		// Parametriza��o b�sica das respostas
		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectResponseTime(Matchers.lessThan(MAX_TIMEOUT)); //tempo m�ximo para responder
		RestAssured.responseSpecification = resBuilder.build();
		
		// esse m�todo configura o a gera��o de log somente em caso de falha do teste
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	} 
}
