package chat_bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

    /** Это класс отвечающий за формирование выводимых сообщений
     *
     */

public class ConsoleHelper {
    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));;

    public static void writeMessage(String message){
        System.out.println(message);
    }

    public static String readString(){
        String string;
        while (true){
        try {
           return string = bufferedReader.readLine();
        } catch (IOException e) {
            writeMessage("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            readString();
        }
      }
    }

    public static int readInt(){
        int read = 0;
        while (true){
            try {
                return read = Integer.parseInt(readString());
            } catch (NumberFormatException e) {
                writeMessage("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
                readString();
            }
        }

    }
}
