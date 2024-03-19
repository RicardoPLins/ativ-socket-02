import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        System.out.println("== Cliente ==");
        Socket socket = new Socket("localhost", 6000);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        while (true) {
            Scanner teclado = new Scanner(System.in);
            dos.writeUTF(teclado.nextLine());
            String mensagemRecebida = dis.readUTF();
            System.out.println(mensagemRecebida);
        }
    }
}
