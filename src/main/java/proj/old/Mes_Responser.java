package proj.old;

public class Mes_Responser {

    public static byte[] response(Package pack) {
        byte[] resp = ("OK").getBytes();
        byte[] encdoedRes = Cipher.encodeMessage(resp);
        Message answer = new Message(pack.getbMsg().getcType(),pack.getbMsg().getbUserId(),encdoedRes);
        Package packResponse = new Package((byte)1, pack.getbPktId(), answer.getMessageBMsq().length, answer);
        return packResponse.packToBytes();
    }
}
