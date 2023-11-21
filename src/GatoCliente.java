import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GatoCliente {
    public static void main(String[] args) throws Exception {
        try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            //BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
            Scanner scanner = new Scanner(System.in);

            System.out.println("Conexion establecida satisfactoriamente.");
            int turno = 1; // contador de turnos
            int contador = 0; // contador 
            while (true) {
                clear(turno);
                // Recibe mensajes del servidor
                String mensajeServidor = in.readLine();

                // Si el servidor solicita un movimiento, solicita entrada al usuario
                if (mensajeServidor != null && mensajeServidor.startsWith("Ingrese")) {
                    contador++;
                    System.out.println("Es su turno. " + mensajeServidor);
                    String movimiento = scanner.next();

                    // Envia el movimiento al servidor
                    out.println(movimiento);
                } else if (mensajeServidor != null && (mensajeServidor.startsWith("Feli") ||
                        mensajeServidor.startsWith("Lo") || mensajeServidor.startsWith("El"))) {
                    // Para detectar el mensaje al verificar ganador
                    System.out.println("\n" + mensajeServidor);
                    Thread.sleep(5000); // Pausa la ejecucion por 3 segundos
                } else if (mensajeServidor != null) {
                    // Si no espera un movimiento, simplemente imprime el mensaje
                    System.out.println("\n" + mensajeServidor);
                } else {
                    // Una vez finalizado el programa, se imprime el mensaje de cierre
                    System.out.println("Conexion terminada por el servidor");
                    break;
                }
                
                if(contador == 2){
                    contador = 0;
                    turno++;
                }
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
        /* Introduce tu código desde aquí */
        System.out.println("Jugador 1: Cliente\n");
        System.out.println("Turno " + turno + "\n---------------------------\n");
    }
}
