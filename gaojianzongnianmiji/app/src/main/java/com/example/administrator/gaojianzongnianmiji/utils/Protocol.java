package com.example.administrator.gaojianzongnianmiji.utils;

/**
 * 协议
 */
public interface Protocol {

    /**
     * 帧头0
     */
    byte FRAME_HEAD_0 = 0x3A;
    String HEAD_0 = "3A";
    /**
     * 帧头1
     */
    byte FRAME_HEAD_1 = (byte) 0xA3;

    byte MIN_PACK_LEN = 0x0F;
    String HEAD_1 = "A3";

    /**
     * 保留字节
     */
    String RETAIN = "000000";

    /**
     * 协议控制字
     */
    String PTROL = "00";

    /**
     * 地址字段
     */
    String ADDR = "0001";



}
