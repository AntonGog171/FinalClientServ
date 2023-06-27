package proj.old;

import javax.crypto.KeyGenerator;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

public class Cipher {
    private static final Key KEY1 = generateSecureRandomKey(256);
    private static Key generateSecureRandomKey(int keySize) {

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = new SecureRandom();
            keyGenerator.init(keySize, secureRandom);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] encodeMessage(byte[] message) { // тут все перелопачено знову, бо минула реалізація шляпа
                                                         // і в нову лабу треба зовсім інше
        try {
            final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, KEY1);
            return cipher.doFinal(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decodeMessage(byte[] message){
        try {
        final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, KEY1);
        return cipher.doFinal(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    private static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }


    // Я замінив реалізацію з лаби 1 бо там по дефолту
    // були нулі в параметрах пакету, бо там це не важливо а тут вже треба
    public static byte[] encode(Message command, byte bSrc, long bPktId, int wLen) {
        try {
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, KEY1);
            byte[] message = encodeMessage(command.getMessage());

            byte[] header = new byte[]{
                    0x13,
                    bSrc,
                    0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0A,
                    0x0, 0x0, 0x0, (byte) message.length
            };
            System.arraycopy(longToBytes(bPktId), 0, header, 2, 8);
            System.arraycopy(intToBytes(wLen), 0, header, 10, 4);
            return ByteBuffer.allocate(header.length + message.length + 4)
                    .put(header)
                    .putShort(CRC16.crc16(header,0,0))
                    .put(message)
                    .putShort(CRC16.crc16(message,0,0))
                    .array();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Message decode(Package pack) {
        try {
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, KEY1);

            byte[] packageBytes = pack.getPackageBytes();

            ByteBuffer comparing = ByteBuffer.wrap(packageBytes);
            byte clientId = comparing.get(1);
            long packetId = comparing.getLong(2);
            int messageLength = comparing.getInt(10);
            short fCRC = comparing.getShort(14);


            ByteBuffer bb= ByteBuffer.allocate(1 + 1 + 8 + 4);
            bb.put((byte) 0x13);
            bb.put(clientId);
            bb.putLong(packetId);
            bb.putInt(messageLength);
            byte[] header =bb.array();
            if (CRC16.crc16(header,0,0) != fCRC) {
                throw new Exception("Invalid CRC of header");
            }

            byte[] message = Arrays.copyOfRange(packageBytes, 16, 16 + messageLength);
            short sCRC = comparing.getShort(16 + messageLength);

            if (CRC16.crc16(message,0,0) != sCRC) {
                throw new Exception("Invalid CRC of message");
            }

            ByteBuffer mBuffer = ByteBuffer.wrap(message);
            int cType = mBuffer.getInt();
            int bUserId = mBuffer.getInt();
            byte[] messageToDecrypt = new byte[messageLength - 8];
            mBuffer.get(8, messageToDecrypt);

            byte[] decryptedMessage = cipher.doFinal(messageToDecrypt);

            return new Message(cType, bUserId, decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

