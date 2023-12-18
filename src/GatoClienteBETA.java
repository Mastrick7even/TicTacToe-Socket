import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GatoClienteBETA {
    public static int SERVIDOR = 1;
    private static char[][] tablero = new char[GatoServidorBETA.TAMANO_TABLERO][GatoServidorBETA.TAMANO_TABLERO];

    public static void main(String[] args) throws Exception {
        try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            System.out.println("Conexion establecida satisfactoriamente.\nEsperando respuesta del servidor...");
            boolean movimientoValido;
            int quienComienza = Integer.parseInt(in.readLine());
            int turno = 1; //* Contador de turnos

            while (true) {
                clear(turno);
                mostrarTablero();
                if (quienComienza == SERVIDOR) {
                    System.out.println("\nEsperando a que servidor termine su turno...");
                } else if(turno > 1){ 
                    //? En el caso de que comience jugando el cliente
                    System.out.println("\nEsperando a que servidor termine su turno...");
                }
                // *Recibe mensajes del servidor
                String mensajeServidor = in.readLine();

                // *Si el servidor solicita un movimiento, solicita entrada al usuario
                if (mensajeServidor != null && mensajeServidor.startsWith("Es su turno")) {
                    clear(turno);
                    recibirTableroDesdeServidor(in);
                    mostrarTablero();
                    System.out.println("\n" + mensajeServidor);
                    int fila;
                    int columna;
                    do {
                        clear(turno);
                        mostrarTablero();
                        movimientoValido = true;
                        System.out.println("\nEs su turno. \nIngrese la fila: ");
                        fila = scanner.nextInt();
                        System.out.println("\nIngrese la columna: ");
                        columna = scanner.nextInt();
                        if (fila < 1 || fila > 3 || columna < 1 || columna > 3) {
                            clear(turno);
                            mostrarTablero();
                            System.out.println("\nMovimiento invalido, ingrese nuevamente");
                            Thread.sleep(2000);
                            movimientoValido = false;
                        } else if ('O' == tablero[fila-1][columna-1] || 'X' == tablero[fila-1][columna-1]) {
                            clear(turno);
                            mostrarTablero();
                            System.out.println("\nLa casilla ya esta ocupada. Intentalo de nuevo.");
                            Thread.sleep(2000);
                            movimientoValido = false;
                        }
                    } while (!movimientoValido);

                    // *Envia el movimiento al servidor
                    out.println(fila);
                    out.println(columna);

                    recibirTableroDesdeServidor(in);
                    mostrarTablero();
                } else if (mensajeServidor != null && (mensajeServidor.startsWith("Felicidades") ||
                        mensajeServidor.startsWith("Lo siento") || mensajeServidor.startsWith("El juego"))) {
                    // *Para detectar el mensaje al verificar ganador
                    turno--;
                    clear(turno);
                    mostrarTablero();
                    System.out.println("\n" + mensajeServidor);
                    Thread.sleep(8000); // Pausa la ejecucion por 6 segundos
                } else if (mensajeServidor != null) {
                    // *Si no espera un movimiento, simplemente imprime el mensaje
                    System.out.println("\n" + mensajeServidor);
                    Thread.sleep(8000);
                } else {
                    // *Una vez finalizado el programa, se imprime el mensaje de cierre
                    clear(turno);
                    mostrarTablero();
                    System.out.println("\nConexion terminada por el servidor");
                    break;
                }
                turno++;
            }
            scanner.close();
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clear(int turno) {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            /* No hacer nada */
        }
        System.out.println("Jugador Cliente\n");
        System.out.println("Turno " + turno + "\n------------------\n");
    }

    private static void mostrarTablero() {
        System.out.println("Estado actual del tablero:\n");

        // Imprimir encabezado de columnas
        System.out.print("   ");
        for (int c = 1; c <= GatoServidorBETA.TAMANO_TABLERO; c++) {
            System.out.print(c);
            if (c < GatoServidorBETA.TAMANO_TABLERO) {
                System.out.print("  | ");
            }
        }
        System.out.println();

        // Imprimir separador entre encabezado y contenido
        System.out.print("  ");
        for (int s = 0; s < GatoServidorBETA.TAMANO_TABLERO - 1; s++) {
            System.out.print("------");
        }
        System.out.println("-");

        // Imprimir filas y contenido del tablero
        for (int i = 0; i < GatoServidorBETA.TAMANO_TABLERO; i++) {
            // Imprimir número de fila
            System.out.print((i + 1) + " | ");

            for (int j = 0; j < GatoServidorBETA.TAMANO_TABLERO; j++) {
                System.out.print(tablero[i][j]);
                if (j < GatoServidorBETA.TAMANO_TABLERO - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();

            // Imprimir separadores entre filas
            if (i < GatoServidorBETA.TAMANO_TABLERO - 1) {
                System.out.print("  ");
                for (int k = 0; k < GatoServidorBETA.TAMANO_TABLERO - 1; k++) {
                    System.out.print("------");
                }
                System.out.println("-");
            }
        }
    }

    private static void recibirTableroDesdeServidor(BufferedReader in) throws IOException {
        for (int i = 0; i < GatoServidorBETA.TAMANO_TABLERO; i++) {
            String fila = in.readLine();
            for (int j = 0; j < GatoServidorBETA.TAMANO_TABLERO; j++) {
                tablero[i][j] = fila.charAt(j);
            }
        }
    }
}
