import java.io.*;
import java.net.*;
import java.util.Locale;

public class Main {
    public static final String ECHO = "echo";
    public static void main(String[] args) {
        try {
            TCPServer tcpServer = new TCPServer(8085);
            while (true) {
                Socket clientSocket = tcpServer.acceptClient();

                // Создаем потоки для чтения и записи данных
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                StringBuilder commandBuffer = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Клиент отправил: " + inputLine);
                    commandBuffer.append(inputLine);
                    String command = commandBuffer.toString().trim();
                    if (command.equalsIgnoreCase("CLOSE") || command.equalsIgnoreCase("EXIT") || command.equalsIgnoreCase("QUIT")){
                        in.close();
                        out.close();
                        clientSocket.close();
                        System.out.println("Соединение с клиентом закрыто.");
                        break;
                    }
                    else if (command.toUpperCase(Locale.ROOT).contains("UPLOAD")) {
                        String[] commandArgs = inputLine.split(" ");
                        tcpServer.receiveFile(clientSocket.getInputStream(), commandArgs[1]);
                        out.println("Файл загружен");
                        commandBuffer.setLength(0); // Очищаем буфер команды
                    }
                    else if (command.toUpperCase(Locale.ROOT).contains("DOWNLOAD")) {
                        String[] commandArgs = inputLine.split(" ");
                        String fileName = commandArgs[1];
                        try {
                            File file = new File(fileName);
                            if(!file.exists())
                                throw new IOException();
                            tcpServer.sendFile(clientSocket.getOutputStream(), fileName);
                            out.println("Файл отправлен");
                        }catch (IOException e){
                            out.println("Файла с таким имененм не существует");
                        } finally {
                            commandBuffer.setLength(0); // Очищаем буфер команды
                        }
                    }
                    else {
                        String response = processCommand(command); // Обработка команды
                        out.println(response); // Отправка ответа клиенту
                        commandBuffer.setLength(0); // Очищаем буфер команды
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для обработки команд
    private static String processCommand(String command) {
        if (command.toUpperCase(Locale.ROOT).contains("ECHO")) {
            String temp = command.replaceAll(ECHO.toUpperCase(Locale.ROOT),"");
            temp = temp.replaceAll(ECHO,"");
            return temp.trim();
        } else if (command.equalsIgnoreCase("TIME")) {
            return "" + new java.util.Date();
        }
        else {
            return "Неизвестная команда: " + command;
        }
    }
}
