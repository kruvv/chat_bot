package chat_bot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static class Handler extends Thread{
        Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws Exception{
            while (true){
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message answer = connection.receive();

                    if(answer.getType() == MessageType.USER_NAME){
                        if(!answer.getData().isEmpty()){
                            if(!connectionMap.containsKey(answer.getData())){
                                connectionMap.put(answer.getData(), connection);
                                connection.send(new Message(MessageType.NAME_ACCEPTED));
                                return answer.getData();
                            }
                        }

                    }
            }

        }

        /** отправка клиенту (новому участнику) информации об
         остальных клиентах (участниках) чата.

         Для этого:
         1) Приватный метод void sendListOfUsers(Connection connection, String userName) throws
         IOException, где connection – соединение с участником, которому будем слать
         информацию, а userName – его имя. Метод должен:
         2) Пройтись по connectionMap.
         3) У каждого элемента из п.2 получить имя клиента, сформировать команду с типом
         USER_ADDED и полученным именем.
         4) Отправить сформированную команду через connection.
         5) Команду с типом USER_ADDED и именем равным userName отправлять не нужно,
         пользователь и так имеет информацию о себе.
         */

        private void sendListOfUsers(Connection connection, String userName)throws Exception{
            for (Map.Entry<String, Connection> item:connectionMap.entrySet()) {
                if(!item.getKey().contains(userName)){
                    connection.send(new Message(MessageType.USER_ADDED, item.getKey()));
                }
            }
        }

        /** Главный цикл обработки сообщений сервером.
         * Он должен:
         * 1) Принимать сообщение клиента
         * 2) Если принятое сообщение – это текст (тип TEXT), то формировать новое
         * текстовое сообщение путем конкатенации: имени клиента, двоеточия, пробела и
         * текста сообщения. Например, если мы получили сообщение с текстом "привет чат" от
         * пользователя "Боб", то нужно сформировать сообщение "Боб: привет чат".
         * 3) Отправлять сформированное сообщение всем клиентам с помощью метода
         * sendBroadcastMessage.
         * 4) Если принятое сообщение не является текстом, вывести сообщение об ошибке
         * 5) Организовать бесконечный цикл.
         * @param connection
         * @param userName
         * @throws Exception
         */

        private void serverMainLoop(Connection connection, String userName) throws Exception {
            while (true){
                Message serverMessage = connection.receive();
                 if(serverMessage != null && serverMessage.getType() == MessageType.TEXT){
                     sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + serverMessage.getData()));
                 }else {ConsoleHelper.writeMessage("Ошибка сообщения сервера");}
            }
        }

        @Override
        public void run() {
            if(socket != null && socket.getRemoteSocketAddress() != null){
                ConsoleHelper.writeMessage("Установлено новое соединение с удаленным адресом: " + socket.getRemoteSocketAddress());
            }
            String user = "";
            try (Connection connection = new Connection(socket)) {
                user = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, user));
                sendListOfUsers(connection, user);
                serverMainLoop(connection, user);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Ошибка обмена данных с удаленным адресом");
            } catch (Exception e) {
                ConsoleHelper.writeMessage("Ошибка обмена данных с удаленным адресом");
            }finally {
                if (user != null){
                    connectionMap.remove(user);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED, user));
                }
                ConsoleHelper.writeMessage("Cоединение с удаленным адресом закрыто");
            }

        }
    }

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message){
        for (Connection connection:connectionMap.values()) {
            try {
                connection.send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Ошибка отправки сообщения");
            }
        }
    }
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt())) {
            ConsoleHelper.writeMessage("Сервер запущен...");
            while (true){
                new Handler(serverSocket.accept()).start();
            }

        } catch (IOException e) {
            ConsoleHelper.writeMessage("Что то не так, соединение закрыто");
        }

    }
}
