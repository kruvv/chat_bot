package chat_bot.client;


import chat_bot.ConsoleHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {

        String nameBot = "date_bot_" + ((int)(Math.random()*100));
        return nameBot;
    }

    public class BotSocketThread extends SocketThread{

        @Override
        protected void processIncomingMessage(String message) {
            if(message != null){
            ConsoleHelper.writeMessage(message);
                SimpleDateFormat simpleDateFormat = null;
            if(message.contains(": ")){
                String[] info = message.split(": ");
                if(info.length == 2 && info[1] != null) {
                    String infoName = info[0];
                    String infoMessage = info[1];
                    if (infoMessage.equalsIgnoreCase("дата")) {
                        simpleDateFormat = new SimpleDateFormat("d.MM.YYYY");
                    }
                    if (infoMessage.equalsIgnoreCase("день")) {
                        simpleDateFormat = new SimpleDateFormat("d");
                    }
                    if (infoMessage.equalsIgnoreCase("месяц")) {
                        simpleDateFormat = new SimpleDateFormat("MMMM");
                    }
                    if (infoMessage.equalsIgnoreCase("год")) {
                        simpleDateFormat = new SimpleDateFormat("YYYY");
                    }
                    if (infoMessage.equalsIgnoreCase("время")) {
                        simpleDateFormat = new SimpleDateFormat("H:mm:ss");
                    }
                    if (infoMessage.equalsIgnoreCase("час")) {
                        simpleDateFormat = new SimpleDateFormat("H");
                    }
                    if (infoMessage.equalsIgnoreCase("минуты")) {
                        simpleDateFormat = new SimpleDateFormat("m");
                    }
                    if (infoMessage.equalsIgnoreCase("секунды")) {
                        simpleDateFormat = new SimpleDateFormat("s");
                    }
                    if (simpleDateFormat != null) {
                        sendTextMessage(String.format("Информация для %s: %s", infoName, simpleDateFormat.format(Calendar.getInstance().getTime())));
                    }

                }
            }

            }

        }

        @Override
        protected void clientMainLoop() {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }
    }


    public static void main(String[] args) {
        new BotClient().run();

    }
}
