@file:Suppress("NoWildcardImports")

package websockets

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.websocket.ClientEndpoint
import jakarta.websocket.ContainerProvider
import jakarta.websocket.OnMessage
import jakarta.websocket.Session
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URI
import java.util.concurrent.CountDownLatch

private val logger = KotlinLogging.logger {}

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ElizaServerTest {
    @LocalServerPort
    private var port: Int = 0

    @Test
    fun onOpen() {
        logger.info { "This is the test worker" }
        val latch = CountDownLatch(3)
        val list = mutableListOf<String>()

        val client = SimpleClient(list, latch)
        client.connect("ws://localhost:$port/eliza")
        latch.await()
        assertEquals(3, list.size)
        assertEquals("The doctor is in.", list[0])
    }

    // @Disabled // Remove this line when you implement onChat
    @Test
    fun onChat() {
        logger.info { "Test thread" }
        val latch = CountDownLatch(4)
        val list = mutableListOf<String>()

        val client = ComplexClient(list, latch)
        client.connect("ws://localhost:$port/eliza")
        latch.await()
        val size = list.size
        // 1. EXPLAIN WHY size = list.size IS NECESSARY

        // El size = list.size es necesario ya que el valor de la variable puede seguir aumentando despues de
        // que latch await() termine su espera, al guardar el tamaño en una variable evitamos este problema ya
        // que trabajamos con un valor fijo de mensajes recibidos.

        // 2. REPLACE BY assertXXX expression that checks an interval; assertEquals must not be used; 

        // Comprobamos que el tamaño de la lista está entre 4 y 6, ya que Eliza puede responder de forma aleatoria
        assertTrue(size in 4..6)

        // 3. EXPLAIN WHY assertEquals CANNOT BE USED AND WHY WE SHOULD CHECK THE INTERVAL

        // No podemos usar assertEquals ya que los mensajes que recibimos de Eliza son aleatorios, es decir, 
        // el numero de mensajes recibidos puede variar en cada ejecución del test, por lo que no podemos
        // asegurar que siempre recibiremos el mismo número de mensajes.

        // 4. COMPLETE assertEquals(XXX, list[XXX])

        // Verificamos que los primeros mensajes son los esperados
        assertEquals("The doctor is in.", list[0])
        assertEquals("What's on your mind?", list[1])
        assertEquals("---", list[2])
    }
}

@ClientEndpoint
class SimpleClient(
    private val list: MutableList<String>,
    private val latch: CountDownLatch,
) {
    @OnMessage
    fun onMessage(message: String) {
        logger.info { "Client received: $message" }
        list.add(message)
        latch.countDown()
    }
}

@ClientEndpoint
class ComplexClient(
    private val list: MutableList<String>,
    private val latch: CountDownLatch,
) {
    @OnMessage
    fun onMessage(
        message: String,
        session: Session,
    ) {
        logger.info { "Client received: $message" }
        list.add(message)
        latch.countDown()
        if (message == "---") {
            session.basicRemote.sendText("I am feeling sad today")
        }
    }
}

fun Any.connect(uri: String) {
    ContainerProvider.getWebSocketContainer().connectToServer(this, URI(uri))
}
