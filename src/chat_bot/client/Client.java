package chat_bot.client;



import chat_bot.Connection;
import chat_bot.ConsoleHelper;
import chat_bot.Message;
import chat_bot.MessageType;

import java.io.IOException;

public class Client extends Thread {
    protected Connection connection;
    private volatile boolean clientConnected = false;


    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
    
    /** Внутренний класс SocketThread унаследованный от Thread в классе
     Client. Он будет отвечать за поток, устанавливающий сокетное соединение и
     читающий сообщения сервера.
     */

    public class SocketThread extends Thread {
       
        protected void clientMainLoop() {
        }

        processIncomingMessage(String message);

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
        }

        // add here code
    }
    
    /** String getServerAddress() – должен запросить ввод адреса сервера у
    пользователя и вернуть введенное значение. Адрес может быть строкой, содержащей
    ip, если клиент и сервер запущен на разных машинах или ‘localhost’, если клиент и
    сервер работают на одной машине.
     */
    
        protected String getServerAddress() {
            ConsoleHelper.writeMessage("Введите адрес сервера:");
            return ConsoleHelper.readString();
        }

        /** int getServerPort() – должен запрашивать ввод порта сервера и возвращать его.
         * 
          * @return
         */

    protected int getServerPort() {
            ConsoleHelper.writeMessage("Введите номер порта:");
            return ConsoleHelper.readInt();
        }

        /** String getUserName() – должен запрашивать и возвращать имя пользователя.
         * 
         * @return
         */

    protected String getUserName() {
            ConsoleHelper.writeMessage("Введите Ваше Имя для доступа в чат:");
            return ConsoleHelper.readString();
        }

        /** boolean shouldSendTextFromConsole() – в данной реализации клиента всегда
         должен возвращать true (мы всегда отправляем текст введенный в консоль).
         */
        
        protected boolean shouldSendTextFromConsole() {
            return true;
        }
        
        /** SocketThread getSocketThread() – должен создавать и возвращать новый объект
         класса SocketThread.
         */

        protected SocketThread getSocketThread() {
            return new SocketThread();
        }
        
        /** void sendTextMessage(String text) – создает новое текстовое сообщение,
         используя переданный текст и отправляет его серверу через соединение connection.
         Если во время отправки произошло исключение IOException, то необходимо вывести
         информацию об этом пользователю и присвоить false полю clientConnected.
         */

        protected void sendTextMessage(String text) {

            try {
                connection.send(new Message(MessageType.TEXT, text));
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Ошибка соединения при отправке сообщения");
                clientConnected = false;
            }
        }
        
        /** Метод должен создавать вспомогательный поток
         SocketThread, ожидать пока тот установит соединение с сервером, а после этого
         в цикле считывать сообщения с консоли и отправлять их серверу. Условием выхода
         из цикла будет отключение клиента или ввод пользователем команды 'exit'.
         Для информирования главного потока, что соединение установлено во
         вспомогательном потоке, используются методы wait и notify объекта класса Client.
         */

        @Override
        public void run() {
            {
                SocketThread socketThread = getSocketThread();
                socketThread.setDaemon(true);
                socketThread.start();
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    ConsoleHelper.writeMessage("Ошибка потока...");
                    System.exit(1);
                }
                if (clientConnected) {
                    ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду ‘exit’");
                    while (clientConnected) {
                        String message = ConsoleHelper.readString();
                        if (message.equalsIgnoreCase("exit")) {
                            break;
                        } else {
                            if (shouldSendTextFromConsole()) {
                                sendTextMessage(message);
                            }
                        }
                    }
                } else {
                    ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
                }
            }
        }
    }


