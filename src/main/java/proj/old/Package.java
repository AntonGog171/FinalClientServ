package proj.old;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Package {
    private static final byte bMagic = 0x13;
    private byte bSrc;

    public long getbPktId() {
        return bPktId;
    }

    private long bPktId;
    private int wLen;
    private short wCrc16;

    public Message getbMsg() {
        return bMsg;
    }

    public byte getbSrc() {
        return bSrc;
    }
    private Message bMsg;
    private short wCrc16_end;
    private byte[] packageBytes;

    public Package(byte[] bytes){
        try{
        if (bytes[0] != bMagic) {
            throw new IllegalArgumentException("Invalid magic byte");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 1, bytes.length - 1);
        this.bSrc = buffer.get();
        this.bPktId = buffer.order(ByteOrder.BIG_ENDIAN).getLong();
        this.wLen = buffer.order(ByteOrder.BIG_ENDIAN).getInt();
        this.wCrc16 = buffer.order(ByteOrder.BIG_ENDIAN).getShort();
        final short wCrc16Counted = CRC16.crc16(bytes, 0, Long.BYTES+Integer.BYTES+Short.BYTES);
        if(wCrc16Counted != this.wCrc16){
            throw new IllegalArgumentException("wCrc16 expected: " + wCrc16Counted + ", but get " + wCrc16);
        }
        this.bMsg = new Message(buffer, this.wLen);
        this.wCrc16_end = buffer.order(ByteOrder.BIG_ENDIAN).getShort();
        final short wCrc16LastCounted = CRC16.crc16(bytes, 0, 2 + Long.BYTES + Integer.BYTES * 3 + Short.BYTES + wLen);
        if(wCrc16LastCounted != wCrc16_end){
            throw new IllegalArgumentException("wCrc16Last expected: " + wCrc16LastCounted + ", but get " + wCrc16_end);
        }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Package(byte bSrc, long bPktId, int wLen, Message bMsq){
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.wLen = wLen;
        this.bMsg = bMsq;
        byte[] header = ByteBuffer.allocate(2 + Long.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES)
                .put(this.bMagic)
                .put(this.bSrc)
                .putLong(this.bPktId)
                .putInt(this.wLen)
                .putInt(bMsq.getcType())
                .putInt(bMsq.getbUserId())
                .array();
        this.wCrc16 = CRC16.crc16(header, 0, header.length - Integer.BYTES * 2);
        byte[] headerWithCrc = ByteBuffer.allocate(2 + Long.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES +
                        Short.BYTES + Cipher.encodeMessage(bMsq.getMessageBMsq()).length)
                .put(this.bMagic)
                .put(this.bSrc)
                .putLong(this.bPktId)
                .putInt(this.wLen)
                .putShort(this.wCrc16)
                .putInt(bMsq.getcType())
                .putInt(bMsq.getbUserId())
                .put(Cipher.encodeMessage(bMsq.getMessageBMsq()))
                .array();
        this.wCrc16_end = CRC16.crc16(headerWithCrc, 0, headerWithCrc.length);
    }

    public byte[] packToBytes() {
        Message message = getbMsg();
        byte[] header = ByteBuffer.allocate(2 + Long.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES)
                .put(this.bMagic)
                .put(this.bSrc)
                .putLong(this.bPktId)
                .putInt(Cipher.encodeMessage(bMsg.getMessageBMsq()).length)
                .putInt(bMsg.getcType())
                .putInt(bMsg.getbUserId())
                .array();
        this.wCrc16 = CRC16.crc16(header, 0, header.length - Integer.BYTES * 2);
        byte[] headerWithCrc = ByteBuffer.allocate(2 + Long.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES +
                        Short.BYTES + Cipher.encodeMessage(bMsg.getMessageBMsq()).length)
                .put(this.bMagic)
                .put(this.bSrc)
                .putLong(this.bPktId)
                .putInt(Cipher.encodeMessage(bMsg.getMessageBMsq()).length)
                .putShort(this.wCrc16)
                .putInt(bMsg.getcType())
                .putInt(bMsg.getbUserId())
                .put(Cipher.encodeMessage(bMsg.getMessageBMsq()))
                .array();
        this.wCrc16_end = CRC16.crc16(headerWithCrc, 0, headerWithCrc.length);
        return ByteBuffer.allocate(2 + Long.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES +
                        Short.BYTES * 2 + Cipher.encodeMessage(bMsg.getMessageBMsq()).length)
                .put(this.bMagic)
                .put(this.bSrc)
                .putLong(this.bPktId)
                .putInt(Cipher.encodeMessage(bMsg.getMessageBMsq()).length)
                .putShort(this.wCrc16)
                .putInt(bMsg.getcType())
                .putInt(bMsg.getbUserId())
                .put(Cipher.encodeMessage(bMsg.getMessageBMsq()))
                .putShort(this.wCrc16_end)
                .array();
    }

    private void validBM(ByteBuffer buffer) throws Exception {
        byte mByte = buffer.get(0);
        if (bMagic !=mByte) {
            throw new Exception("Wrong magic byte");
        }
    }

    public byte[] getPackageBytes() {
        return packageBytes;
    }

    @Override
    public String toString() {
        return "old.old.Package{" +
                "bMagic=" + bMagic +
                ", bSrc=" + bSrc +
                ", bPktId=" + bPktId +
                ", wLen=" + wLen +
                ", wCrc16=" + wCrc16 +
                ", bMsg=" + bMsg.getText() +
                ", wCrc16_end=" + wCrc16_end +
                '}';
    }
}

