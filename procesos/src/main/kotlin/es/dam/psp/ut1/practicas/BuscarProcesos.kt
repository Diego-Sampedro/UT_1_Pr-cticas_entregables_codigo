package es.dam.psp.ut1.practicas

/**
 * E6 · Un "ps" casero con ProcessHandle: información de los procesos del sistema.
 *
 * Apuntes UT-1, apartado 6.4. Base de la Práctica 2.c (practicas/BuscarProcesos.kt).
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Muestra el PID propio y el del padre, y una tabla PID, PPID, USUARIO y COMANDO con los
 * 15 primeros procesos visibles. Solo verás los datos que el sistema operativo permite leer
 * a tu usuario: cada dato de info() es un Optional que puede estar vacío.
 */
fun main() {


    val cadena = "java"

    println("Procesos cuyo comando contiene: \"$cadena\" (Diego Sampedro Kotlin)")
    println(
        "%-8s %-8s %-12s %-25s %-15s %s".format(
            "PID",
            "PPID",
            "USUARIO",
            "INICIO",
            "CPU",
            "COMANDO"
        )
    )

    ProcessHandle.allProcesses()
        .forEach { p ->
            val info = p.info()
            if (info.command().isPresent &&
                info.command().get().contains(cadena, ignoreCase = true)
            ) {
                println(
                    "%-8s %-8s %-12s %-25s %-15s %s".format(
                        // PID
                        p.pid(),

                        // PPID
                        p.parent()
                            .map { it.pid().toString() }
                            .orElse("-"),

                        // Usuario
                        info.user().orElse("?").takeLast(12),

                        // Hora de inicio
                        info.startInstant()
                            .map { it.toString() }
                            .orElse("-"),

                        // Tiempo total de CPU
                        info.totalCpuDuration()
                            .map { it.toString() }
                            .orElse("-"),

                        // Nombre del comando
                        info.command().get().takeLast(60)
                    )
                )
            }
        }
}
