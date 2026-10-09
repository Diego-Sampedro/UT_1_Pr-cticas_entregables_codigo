package es.dam.psp.ut1.practicas

import es.dam.psp.ut1.Jvm
import java.io.File
import java.util.concurrent.TimeUnit
// Necesitarás además: import java.io.File y import java.util.concurrent.TimeUnit

/**
 * Lanzador.kt · Práctica 3 · Lanzador de órdenes. Completa los TODO 3.x (letras de la práctica).
 *
 * Apuntes UT-1, apartados 5.1 (ProcessBuilder, Jvm.comandoShell), 5.3 (leer la salida),
 * 6.1 (waitFor con límite, destroy) y 6.2 (no bloquearse leyendo).
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos; escribe las órdenes en la consola
 * de IntelliJ (p. ej. "echo hola", "ls noexiste", "sleep 10" o, en Windows, "ping -n 11 127.0.0.1").
 *
 * Bucle: pide una orden al usuario, la ejecuta como proceso hijo y muestra
 *   - su salida (stdout y stderr juntas), con cada línea numerada (3.a),
 *   - su código de salida y el tiempo que ha tardado en milisegundos (3.b).
 * Si la orden tarda más de TIMEOUT_S segundos, se termina el proceso y se avisa (3.c).
 * Cada ejecución se añade a historial.txt con el formato:  orden;codigo;milisegundos (3.d)
 * La orden "salir" termina el programa.
 */

const val TIMEOUT_S = 5L   // segundos que se deja trabajar a cada orden

fun main() {
    println("Lanzador PSP | sistema: ${System.getProperty("os.name")} | escribe 'salir' para terminar")

    while (true) {
        print("> ")
        val orden = readlnOrNull()?.trim() ?: break

        if (orden == "salir") break
        if (orden.isEmpty()) continue

        // 3.c Preparar proceso
        val ficheroTemporal = File.createTempFile("lanzador", ".txt")

        val pb = ProcessBuilder(Jvm.comandoShell(orden))
            .redirectErrorStream(true)
            .redirectOutput(ficheroTemporal)

        // 3.b Arrancar y medir tiempo
        val inicio = System.nanoTime()
        val proceso = pb.start()

        // 3.c Timeout
        val terminado = proceso.waitFor(TIMEOUT_S, TimeUnit.SECONDS)

        val codigo: Int

        if (terminado) {
            codigo = proceso.exitValue()
        } else {
            println("La orden ha superado el límite de $TIMEOUT_S segundos.")

            proceso.descendants().forEach { descendiente ->
                descendiente.destroy()
            }

            proceso.destroy()

            if (proceso.isAlive) {
                proceso.destroyForcibly()
            }

            codigo = -1
        }

        // 3.b Tiempo transcurrido
        val milisegundos =
            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicio)

        // 3.a Mostrar salida numerada
        var numeroLinea = 1

        ficheroTemporal.forEachLine { linea ->
            println("${numeroLinea++}: $linea")
        }

        ficheroTemporal.delete()

        // 3.b Mostrar código y tiempo
        println("[código $codigo - $milisegundos ms]")

        // 3.d Historial
        File("historial.txt").appendText(
            "$orden;$codigo;$milisegundos${System.lineSeparator()}"
        )
    }

    println("Hasta luego")
}
