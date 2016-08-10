package br.com.alura.loja.test;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import br.com.alura.loja.modelo.Projeto;
import br.com.alura.loja.resource.Servidor;

public class ClientServerLocalTest {
	
	HttpServer server;
	Client client;
	WebTarget target;
	
	@Before
	public void startaServidor(){
		this.server = Servidor.inicializaServidor();
		this.client = ClientBuilder.newClient();
        target = this.client.target("http://localhost:8080");
	}
	
	@After
	public void mataServidor(){
		this.server.stop();		
	}
		
	@Test
	public void testBuscaPrimeiroProjeto() throws IOException{
		String conteudo = this.target.path("/projetos/1").request().get(String.class);
		Projeto projeto = (Projeto) new XStream().fromXML(conteudo);
		Assert.assertEquals("Minha loja" , projeto.getNome());
	}
	
    @Test
    public void testQueBuscaUmCarrinhoETrazOCarrinhoEsperado() {
        String conteudo = this.target.path("/carrinhos/1").request().get(String.class);
        Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
        Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
    }
    
    @Test
    public void testDeAdicaoDeNovoCarrinho(){
    	Carrinho carrinho = new Carrinho();
    	carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
    	carrinho.setRua("Rua Vergueiro");
    	carrinho.setCidade("São Paulo");
    	String xml = carrinho.toXML();
    	
    	Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
    	Response response = this.target.path("/carrinhos").request().post(entity);
    	Assert.assertEquals("<status>sucesso</status>", response.readEntity(String.class));
    }
}
