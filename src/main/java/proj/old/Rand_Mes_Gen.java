package proj.old;
import java.util.Random;

public class Rand_Mes_Gen {
    final static int maxUsers = 2000;
    private static int num = 0;
    public static Package generateFakeMessage(){
        num ++;
        Random random = new Random();
        int command = random.nextInt(Message.commands.values().length);
        String commandMsg = (Message.commands.values()[command]).toString();
        if (num >= 20){
            commandMsg = ("end");
        }
        Message testMessage = new Message(command , random.nextInt(maxUsers), Cipher.encodeMessage(commandMsg.getBytes()));
        long left = 10L;
        long right = 100L;
        long glong = left + (long) (random.nextDouble() * (right - left));
        return new Package((byte)1, glong, testMessage.getMessageBMsq().length, testMessage);
    }
}
