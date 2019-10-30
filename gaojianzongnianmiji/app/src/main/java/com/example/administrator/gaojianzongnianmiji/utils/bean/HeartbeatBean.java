package com.example.administrator.gaojianzongnianmiji.utils.bean;

public class HeartbeatBean {
    public int storehousestatus = 1;
    public int status = 3;//1：空闲 2：使用中 3：故障
    public long lastTime;
    public int riceDoorState;
    public int outRiceState;
    public String remarks = "测试设备或者串口未打开的设备";//故障原因
    public String info_msg = "测试设备或者串口未打开的设备";//设备状态信息
    public String data = "";//心跳数据
}
