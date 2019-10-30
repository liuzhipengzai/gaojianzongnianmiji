package com.example.administrator.gaojianzongnianmiji.utils;

import com.dlc.serialportlibrary.ReceiveCallback;
import com.licheedev.myutils.LogPlus;
import java.nio.ByteBuffer;

/**
 * 数据接收器实现类
 * author: John
 * create time: 2018/7/5 14:15
 * description:
 */
public abstract class DataReceiver3BB3 implements ReceiveCallback {

    private final ByteBuffer mByteBuffer;

    public DataReceiver3BB3() {
        mByteBuffer = ByteBuffer.allocate(1024);
        mByteBuffer.clear();
    }

    public abstract void onReceiveValidData(byte[] allPack, byte[] data);

    public void resetCache() {
        mByteBuffer.clear();
    }

    @Override
    public void onReceive(String devicePath, String baudrateString, byte[] received, int size) {

        LogPlus.i("DataReceiver", "接收数据=" + ByteUtil.bytes2HexStr(received, 0, size));
        try {
            mByteBuffer.put(received, 0, size);
            mByteBuffer.flip();
            byte b;
            int readable;
            while ((readable = mByteBuffer.remaining()) >= Protocol.MIN_PACK_LEN) {
                mByteBuffer.mark(); // 标记一下开始的位置
                int frameStart = mByteBuffer.position();

                b = mByteBuffer.get();
                if (b != Protocol.FRAME_HEAD_0) { // 第1个byte要3B
                    continue;
                }

                b = mByteBuffer.get();
                if (b != Protocol.FRAME_HEAD_1) { // 第2个byte要B3
                    continue;
                }

                // 数据长度
                byte[] len = new byte[2];
                len[0] = mByteBuffer.get(frameStart + 8);
                len[1] = mByteBuffer.get(frameStart + 9);
                final int cmdDataLen = byteToInt2(len);
                // 总数据长度
                int total = 12 + cmdDataLen;
                // 如果可读数据小于总数据长度，表示不够,还有数据没接收
                if (readable < total) {
                    // 重置一下要处理的位置,并跳出循环
                    mByteBuffer.reset();
                    break;
                }

                // 回到头
                mByteBuffer.reset();
                // 拿到整个包
                byte[] allPack = new byte[total];
                mByteBuffer.get(allPack);

                String myCrc16 = CRC16Utils.getCRC16(allPack, 0, total - 2);
                String reciveCrc16 = ByteUtil.bytes2HexStr(allPack, total - 2, 2);
                // 校验通过
                if (myCrc16.equals(reciveCrc16)) {
                    final byte[] data = new byte[cmdDataLen];
                    System.arraycopy(allPack, 10, data, 0, data.length);
                    // 收到有效数据
                    onReceiveValidData(allPack, data);
                } else {
                    // 不一致则回到“第二位”，继续找到下一个3BB3
                    mByteBuffer.position(frameStart + 2);
                }
            }
            // 最后清掉之前处理过的不合适的数据
            mByteBuffer.compact();
        } catch (Exception e) {
            e.printStackTrace();
            mByteBuffer.compact();
        }
    }


    public static int byteToInt2(byte[] b) {
        int mask = 0xff;
        int temp;
        int n = 0;
        for (int i = 0; i < b.length; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }
}
