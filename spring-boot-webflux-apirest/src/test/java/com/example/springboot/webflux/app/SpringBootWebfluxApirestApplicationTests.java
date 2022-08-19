package com.example.springboot.webflux.app;

import com.example.springboot.webflux.app.models.documents.Categoria;
import com.example.springboot.webflux.app.models.documents.Producto;
//import org.junit.jupiter.api.Assertions;
import com.example.springboot.webflux.app.models.services.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluxApirestApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ProductoService service;

	@Value("${config.base.endpoint}")
	private String url;

	@Test
	void listarTest() {
		client.get()
				.uri(url)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBodyList(Producto.class)
				.consumeWith(response ->{
					List<Producto> productos = response.getResponseBody();
					productos.forEach(p ->{
						System.out.println(p.getNombre());
					});
					assertThat(productos.size()> 0).isTrue();
				});
				//.hasSize(9);

	}

	@Test
	void verTest() {
		Producto producto = service.findByNombre("TV Panasonic Pantalla LCD").block();

		client.get()
				.uri(url + "/{id}", Collections.singletonMap("id", producto.getId()))
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				/*.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.nombre").isEqualTo("TV Panasonic Pantalla LCD")*/
				.expectBody(Producto.class)
				.consumeWith(response ->{
					Producto p = response.getResponseBody();
					assertThat(p.getId()).isNotEmpty();
					assertThat(p.getId().length()>0).isTrue();
					assertThat(p.getNombre()).isEqualTo("TV Panasonic Pantalla LCD");
				});

		;
	}

	@Test
	void crearTest() {

		Categoria categoria = service.findByCategoriaNombre("Muebles").block();

		Producto producto = new Producto("Mesa comedor", 100.00, categoria);

		client.post().uri(url)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.body(Mono.just(producto), Producto.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBody()
				.jsonPath("$.producto.id").isNotEmpty()
				.jsonPath("$.producto.nombre").isEqualTo("Mesa comedor")
				.jsonPath("$.producto.categoria.nombre").isEqualTo("Muebles");

	}

	@Test
	void crear2Test() {

		Categoria categoria = service.findByCategoriaNombre("Muebles").block();

		Producto producto = new Producto("Mesa comedor", 100.00, categoria);

		client.post().uri(url)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.body(Mono.just(producto), Producto.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBody(new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {
				})
				.consumeWith(response ->{
					Object o = response.getResponseBody().get("producto");
					Producto p = new ObjectMapper().convertValue(o, Producto.class);
					assertThat(p.getId()).isNotEmpty();
					//assertThat(p.getId().length()>0).isTrue();
					assertThat(p.getNombre()).isEqualTo("Mesa comedor");
					assertThat(p.getCategoria().getNombre()).isEqualTo("Muebles");
				});


	}


	@Test
	void editarTest() {
		Producto producto = service.findByNombre("Sony Notebook").block();
		Categoria categoria = service.findByCategoriaNombre("Electrónico").block();

		Producto productoEditado = new Producto("Asus Notebook", 800.00, categoria);

		client.put()
				.uri(url +"/{id}", Collections.singletonMap("id", producto.getId()))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.body(Mono.just(productoEditado), Producto.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.nombre").isEqualTo("Asus Notebook")
				.jsonPath("$.categoria.nombre").isEqualTo("Electrónico");

	}

	@Test
	void eliminarTest() {
		Producto producto = service.findByNombre("Mica Cómoda 5 Cajones").block();
		client.delete()
				.uri(url+"/{id}", Collections.singletonMap("id", producto.getId()))
				.exchange()
				.expectStatus().isNoContent()
				.expectBody()
				.isEmpty();


		client.get()
				.uri(url+"/{id}", Collections.singletonMap("id", producto.getId()))
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.isEmpty();

	}

}
