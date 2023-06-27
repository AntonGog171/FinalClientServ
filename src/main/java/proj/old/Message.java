package proj.old;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

public class Message {

    public int cType;

    public enum commands {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        LIST_BY_CRITERIA
    }
    private int bUserId;
    private byte[] message;

    public String getText(){
        return new String(message);
    }

    public Message(ByteBuffer byteBuffer, int wLen) {
        this.cType = byteBuffer.order(ByteOrder.BIG_ENDIAN).getInt();
        this.bUserId = byteBuffer.order(ByteOrder.BIG_ENDIAN).getInt();
        byte[] encodedMessage = new byte[wLen];
        byteBuffer.get(encodedMessage);
        this.message = Cipher.decodeMessage(encodedMessage);
    }

    public Message(int cType, int bUserId, byte[] message)  {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = Cipher.decodeMessage(message);
    }

    public int getcType() {
        return cType;
    }

    public byte[] getMessageBMsq() {
        return message;
    }

    public int getbUserId() {
        return bUserId;
    }

    public byte[] getMessage() {
        return message;
    }


    @Override
    public String toString() {
        return "old.old.Message{" +
                "cType=" + cType +
                ", bUserId=" + bUserId +
                ", message=" + Arrays.toString(message) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return cType == message1.cType &&
                bUserId == message1.bUserId &&
                Arrays.equals(message, message1.message);
    }

    @Override
    public int hashCode() {
        return 31*Objects.hash(cType, bUserId)+Arrays.hashCode(message);
    }

    public byte[] getMessageBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(2 + message.length);
        buffer.put((byte) cType);
        buffer.put((byte) bUserId);
        buffer.put(message);
        return buffer.array();
    }
}
