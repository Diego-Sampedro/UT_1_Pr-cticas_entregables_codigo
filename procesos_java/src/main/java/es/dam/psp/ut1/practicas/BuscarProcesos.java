package es.dam.psp.ut1.practicas;

/**
 * E6 · Un "ps" casero con ProcessHandle: información de los procesos del sistema.
 *
 * Apuntes UT-1, apartado 6.4. Base de la Práctica 2.c (practicas/BuscarProcesos.java).
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Muestra el PID propio y el del padre, y una tabla PID, PPID, USUARIO y COMANDO con los
 * 15 primeros procesos visibles. Solo verás los datos que el sistema operativo permite leer
 * a tu usuario: cada dato de info() es un Optional que puede estar vacío.
 */
public class BuscarProcesos {

    public static void main(String[] args) {
        ProcessHandle yo = ProcessHandle.current();

        String cadena = "java";

        System.out.println("Procesos cuyo comando contiene: "+cadena+" (Diego Sampedro Java)");
        System.out.printf("%-8s %-8s %-12s %-25s %-15s %s%n", "PID", "PPID", "USUARIO", "INICIO", "CPU","COMANDO");

        ProcessHandle.allProcesses()
                .forEach(p -> {
                    ProcessHandle.Info info = p.info();

                    if (info.command().isPresent()
                            && info.command().get().contains(cadena)) {
                        System.out.printf("%-8s %-8s %-12s %-25s %-15s %s%n",
                                // PID
                                p.pid(),

                                // PPID
                                p.parent().map(padre -> String.valueOf(padre.pid())).orElse("-"),

                                // Usuario
                                ultimos(info.user().orElse("?"), 12),

                                // Hora de inicio
                                info.startInstant().map(it -> String.valueOf(it.toString())).orElse("-"),

                                // Tiempo total de CPU
                                info.totalCpuDuration().map(it -> String.valueOf(it.toString())).orElse("-"),

                                // Nombre del comando
                                ultimos(info.command().get(), 60));
                    }
                });
    }

    /** Los n últimos caracteres de s (o s entera si es más corta). Equivale a takeLast(n) de Kotlin. */
    static String ultimos(String s, int n) {
        return s.length() <= n ? s : s.substring(s.length() - n);
    }
}
