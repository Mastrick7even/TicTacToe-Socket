
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class GatoServidor {
    public static int TAMANO_TABLERO = 3;
    public static Scanner scanf = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Esperando a que el cliente se conecte...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado.");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            char[][] tablero = new char[3][3];
            inicializarTablero(tablero);
            int turno = 1; // contador de turnos
            while (true) {
                clear(turno);
                mostrarTablero(tablero);
                System.out.println("\nEsperando a que cliente finalice su turno...\n");
                // Mensajes y datos que se le envian/piden al cliente
                //out.println("\n\n\nTurno " + turno + "\n---------------------------\n");

                out.println("Ingrese la fila: ");
                int fila = Integer.parseInt(in.readLine());

                out.println("Ingrese la columna: ");
                int columna = Integer.parseInt(in.readLine());

                // Aqui recibo desde el cliente
                if (tablero[fila][columna] == 32) {
                    tablero[fila][columna] = 'X';
                } else {
                    out.println("La casilla ya esta ocupada. Intentalo de nuevo.");
                    continue;
                }

                if (verificarGanador(tablero, 'X')) {
                    mostrarTablero(tablero);
                    out.println("Felicidades, Eres el ganador!");
                    System.out.println("\nLo siento, el Cliente ha ganado");
                    System.out.println("\nConexion terminada");
                    break;
                }

                if (tableroCompleto(tablero)) {
                    mostrarTablero(tablero);
                    out.println("El juego ha terminado en empate!");
                    System.out.println("\nEl juego ha terminado en empate");
                    System.out.println("\nConexion terminada");
                    break;
                }
                clear(turno);
                System.out.println("Movimiento del cliente:");
                mostrarTablero(tablero);

                // Turno del servidor
                realizarJugadaServidor(tablero);

                if (verificarGanador(tablero, 'O')) {
                    mostrarTablero(tablero);
                    out.println("Lo siento, el servidor ha ganado");
                    System.out.println("\nFelicidades, Eres el ganador!");
                    System.out.println("\nConexion terminada");
                    break;
                }
                turno++;
                clear(turno);
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

    private static void realizarJugadaServidor(char[][] tablero) {
        int fila, columna;
        while (true) {
            System.out.println("Es su turno. Ingrese la fila: ");
            fila = scanf.nextInt();
            System.out.println("\n");
            System.out.println("Ingrese la columna: ");
            columna = scanf.nextInt();
            if (fila >= 0 && fila <= TAMANO_TABLERO && columna >= 0 && columna <= TAMANO_TABLERO) {
                if (tablero[fila][columna] == 32) {
                    tablero[fila][columna] = 'O';
                    break;
                } else {
                    System.out.println("La casilla esta ocupada, intenta nuevamente");
                }
            } else {
                System.out.println("Movimiento invalido, ingrese nuevamente");
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
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(tablero[i][j]);
                if (j < 2) {
                    System.out.print(" | ");
                }
            }
            System.out.println();

            if (i < 2) {
                System.out.println("---------");
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
        /* Introduce tu código desde aquí */
        System.out.println("Jugador 2: Servidor\n");
        System.out.println("Turno " + turno + "\n---------------------------\n");
    }
}
