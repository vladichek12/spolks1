import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPServer {
    private final ServerSocket serverSocket;

    public TCPServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public Socket acceptClient() throws IOException {
        return this.serverSocket.accept();
    }

    public  void receiveFile(InputStream inputStream, String fileName) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (new String(buffer, 0, bytesRead, StandardCharsets.UTF_8).contains("EOF")) {
                    // Если в данных найден EOF, завершаем запись в файл
                    break;
                }
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Файл " + fileName + " успешно получен с клиента");
        }
    }

    public  void sendFile(OutputStream outputStream, String fileName) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Файл " + fileName + " успешно отправлен.");
            outputStream.write("EOF".getBytes(StandardCharsets.UTF_8));
        }
    }

}
