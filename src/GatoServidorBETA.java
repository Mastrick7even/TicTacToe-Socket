
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class GatoServidorBETA {
    public static int SERVIDOR = 1;
    public static int TAMANO_TABLERO = 3;
    public static Scanner scanf = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Esperando a que el cliente se conecte...\n");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado desde la dirección: " + clientSocket.getInetAddress().getHostAddress() + "\n");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            char[][] tablero = new char[3][3];
            inicializarTablero(tablero);
            int turno = 1; //* Contador de turnos

            //! Decidir quien comienza jugando 'X'
            int primeraJugada;
            do {
                System.out.println("Quien comenzara jugando 'X'?");
                System.out.println("1. Servidor\n2. Cliente");
                primeraJugada = scanf.nextInt(); // 1. servidor 2. cliente
                if (primeraJugada != 1 && primeraJugada != 2) {
                    System.out.println("Error, recuerda ingresar los valores 1 o 2");
                    Thread.sleep(2000);
                    try { //? Limpiar pantalla
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    } catch (Exception e) {
                        /* No hacer nada */
                    }
                }
            } while (primeraJugada != 1 && primeraJugada != 2);
            out.println(primeraJugada);

            while (true) {
                // ! CASO EN DONDE COMIENZA JUGANDO EL SERVIDOR
                if (primeraJugada == SERVIDOR) {
                    realizarJugadaServidor(tablero, turno, primeraJugada);
                    if (verificarGanador(tablero, 'X')) {
                        clear(turno);
                        mostrarTablero(tablero);
                        out.println("Lo siento, el servidor ha ganado");
                        System.out.println("\nFelicidades, Eres el ganador!");
                        System.out.println("\nConexion terminada");
                        break;
                    }
                    clear(turno);
                    mostrarTablero(tablero);
                    System.out.println("\nEsperando a que cliente finalice su turno...\n");

                    // ? Mensajes y datos que se le envian/piden al cliente
                    out.println("Es su turno ");
                    enviarTableroAlCliente(out, tablero);
                    int fila = Integer.parseInt(in.readLine());
                    int columna = Integer.parseInt(in.readLine());

                    // ? Aqui recibo desde el cliente y marco en el tablero
                    tablero[fila-1][columna-1] = 'O';

                    // ? Después de que el cliente haya ingresado su movimiento
                    clear(turno);
                    mostrarTablero(tablero);
                    enviarTableroAlCliente(out, tablero);

                    if (verificarGanador(tablero, 'O')) {
                        clear(turno);
                        mostrarTablero(tablero);
                        out.println("Felicidades, Eres el ganador!");
                        System.out.println("\nLo siento, el Cliente ha ganado");
                        System.out.println("\nConexion terminada");
                        break;
                    }

                    if (tableroCompleto(tablero)) {
                        clear(turno);
                        mostrarTablero(tablero);
                        out.println("El juego ha terminado en empate!");
                        System.out.println("\nEl juego ha terminado en empate");
                        System.out.println("\nConexion terminada");
                        break;
                    }
                    turno++;

                } else {
                    // ! CASO EN EL QUE COMIENZA JUGANDO EL CLIENTE
                    clear(turno);
                    mostrarTablero(tablero);
                    System.out.println("\nEsperando a que cliente finalice su turno...\n");

                    // * Mensajes y datos que se le envian/piden al cliente
                    out.println("Es su turno ");
                    enviarTableroAlCliente(out, tablero);
                    int fila = Integer.parseInt(in.readLine());
                    int columna = Integer.parseInt(in.readLine());

                    // * Aqui recibo desde el cliente y marco en el tablero
                    tablero[fila-1][columna-1] = 'X';

                    // * Después de que el cliente haya ingresado su movimiento
                    clear(turno);
                    mostrarTablero(tablero);
                    enviarTableroAlCliente(out, tablero);

                    if (verificarGanador(tablero, 'X')) {
                        turno--;
                        clear(turno);
                        mostrarTablero(tablero);
                        out.println("Felicidades, Eres el ganador!");
                        System.out.println("\nLo siento, el Cliente ha ganado");
                        System.out.println("\nConexion terminada");
                        break;
                    }

                    if (tableroCompleto(tablero)) {
                        clear(turno);
                        mostrarTablero(tablero);
                        out.println("El juego ha terminado en empate!");
                        System.out.println("\nEl juego ha terminado en empate");
                        System.out.println("\nConexion terminada");
                        break;
                    }

                    // * Turno del servidor
                    realizarJugadaServidor(tablero, turno, primeraJugada);

                    if (verificarGanador(tablero, 'O')) {
                        clear(turno);
                        mostrarTablero(tablero);
                        out.println("Lo siento, el servidor ha ganado");
                        System.out.println("\nFelicidades, Eres el ganador!");
                        System.out.println("\nConexion terminada");
                        break;
                    }
                    turno++;
                }
            }

            scanf.close();
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void realizarJugadaServidor(char[][] tablero, int turno, int primeraJugada)
            throws InterruptedException {
        int fila, columna;
        while (true) {
            clear(turno);
            mostrarTablero(tablero);
            System.out.println("\nEs su turno. \nIngrese la fila: ");
            fila = scanf.nextInt();
            System.out.println("\nIngrese la columna: ");
            columna = scanf.nextInt();
            if (fila >= 1 && fila <= TAMANO_TABLERO && columna >= 1 && columna <= TAMANO_TABLERO) {
                if (tablero[fila-1][columna-1] == 32) {
                    if (primeraJugada == SERVIDOR) {
                        tablero[fila-1][columna-1] = 'X';
                        break;
                    } else {
                        tablero[fila-1][columna-1] = 'O';
                        break;
                    }
                } else {
                    clear(turno);
                    mostrarTablero(tablero);
                    System.out.println("\nLa casilla esta ocupada, intenta nuevamente");
                    Thread.sleep(2000);
                }
            } else {
                clear(turno);
                mostrarTablero(tablero);
                System.out.println("\nMovimiento invalido, ingrese nuevamente");
                Thread.sleep(2000);
            }
        }
    }

    private static boolean tableroCompleto(char[][] tablero) {
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                if (tablero[i][j] == 32) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean verificarGanador(char[][] tablero, char c) {
        int cont = 0;
        // Horizontal
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                if (tablero[i][j] == c) {
                    cont++;
                }
            }
            if (cont == 3) {
                return true;
            } else {
                cont = 0;
            }
        }

        // Vertical
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                if (tablero[j][i] == c) {
                    cont++;
                }
            }
            if (cont == 3) {
                return true;
            } else {
                cont = 0;
            }
        }

        // Diagonal
        if ((tablero[0][0] == c && tablero[1][1] == c && tablero[2][2] == c) ||
                (tablero[2][0] == c && tablero[1][1] == c && tablero[0][2] == c)) {
            return true;
        }
        return false;
    }

    private static void mostrarTablero(char[][] tablero) {
        System.out.println("Estado actual del tablero:\n");

        // Imprimir encabezado de columnas
        System.out.print("   ");
        for (int c = 1; c <= tablero.length; c++) {
            System.out.print(c);
            if (c < tablero.length) {
                System.out.print("  | ");
            }
        }
        System.out.println();

        // Imprimir separador entre encabezado y contenido
        System.out.print("  ");
        for (int s = 0; s < tablero.length - 1; s++) {
            System.out.print("------");
        }
        System.out.println("-");

        // Imprimir filas y contenido del tablero
        for (int i = 0; i < tablero.length; i++) {
            // Imprimir número de fila
            System.out.print((i + 1) + " | ");

            for (int j = 0; j < tablero[i].length; j++) {
                System.out.print(tablero[i][j]);
                if (j < tablero[i].length - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();

            // Imprimir separadores entre filas
            if (i < tablero.length - 1) {
                System.out.print("  ");
                for (int k = 0; k < tablero.length - 1; k++) {
                    System.out.print("------");
                }
                System.out.println("-");
            }
        }
    }

    private static void inicializarTablero(char[][] tablero) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tablero[i][j] = 32;
            }
        }
    }

    public static void clear(int turno) {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            /* No hacer nada */
        }
        System.out.println("Jugador Servidor\n");
        System.out.println("Turno " + turno + "\n------------------\n");
    }

    private static void enviarTableroAlCliente(PrintWriter out, char[][] tablero) {
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                out.print(tablero[i][j]);
            }
            out.println();
        }
        out.flush();
    }
}
