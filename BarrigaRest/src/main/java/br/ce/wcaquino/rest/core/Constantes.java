package br.ce.wcaquino.rest.core;

import io.restassured.http.ContentType;

/* Essa interface foi projetada para armazenar todos os parâmetros comuns que deverão ser utilizados  
 * no decorrer do projeto
 * */

public interface Constantes {
	//URL base que será utilizada no projeto
	String APP_BASE_URL = "https://barrigarest.wcaquino.me";
	//Porta
	Integer APP_PORT = 443; //HTTP -> PORTA 80
	//caminhos de pacotes de destino da rota API 
	String APP_BASE_PATH = "";
	
	//tipo de arquivos 
	ContentType APP_CONTENT_TYPE = ContentType.JSON;
	
	//tempo máximo de resposta
	Long MAX_TIMEOUT = 5000L;
}
