package com.example.administrator.gaojianzongnianmiji.utils;

import android.text.TextUtils;
import android.util.Log;
import com.dlc.serialportlibrary.DLCSerialPortUtil;
import com.dlc.serialportlibrary.SerialPortManager;
import com.licheedev.myutils.LogPlus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SerialPortUtil {
    private SerialPortManager manager;
    private int timeOut = 0;
    private volatile List<String> cmdPacks = new ArrayList<>();
    private volatile String lastPackHexStr = "";
    private operationResult mResult;
    private checkResult checkResult;
    private alarmResult alarmResult;
    private int maxTime = 10;
    private final StringBuilder stringBuilder = new StringBuilder().append(Protocol.HEAD_0)
            .append(Protocol.HEAD_1)
            .append(Protocol.RETAIN)
            .append(Protocol.PTROL)
            .append(Protocol.ADDR);
    private static volatile SerialPortUtil portUtil;
    private Disposable disposable;

    public static SerialPortUtil getInstance() {
        if (portUtil == null) {
            synchronized (SerialPortUtil.class) {
                if (portUtil == null) {
                    portUtil = new SerialPortUtil();
                }
            }
        }
        return portUtil;
    }

    public boolean isOpenSuccess() {
        if (manager != null) {
            return manager.isOpenSuccess();
        }
        return false;
    }

    public void openSerialPort() {
        try {
            //manager = DLCSerialPortUtil.getInstance().open("/dev/ttyUSB0", "115200");
            manager = DLCSerialPortUtil.getInstance().open("/dev/ttyS4", "115200");
            if (manager != null && manager.isOpenSuccess()) {
                startTimer();
                manager.setReceiveCallback(new DataReceiver3BB3() {
                    @Override
                    public void onReceiveValidData(byte[] allPack, byte[] data) {
                        try {
                            String d = ByteUtil.bytes2HexStr(data);
                            String command = d.substring(0, 4);
                            if (mResult != null) {
                                mResult.receiveResult(d);
                            }
                            if (checkResult != null) {
                                checkResult.receiveResult(d);
                            }
                            if (alarmResult != null){
                                alarmResult.receiveResult(d);
                            }
                            if (!command.equals("0902") && !TextUtils.isEmpty(lastPackHexStr)) {
                                if (command.equals(lastPackHexStr.substring(20, 24))) {
                                    cmdPacks.remove(lastPackHexStr);
                                    timeOut = 0;
                                    lastPackHexStr = "";
                                }
                            }
                        } catch (Exception e) {
                            LogPlus.e("setReceiveCallbackexception" + e.toString());
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                if (mResult != null) {
                    mResult.openFail();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCA(int water, int weight, long precision) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder)
                .append("000E09CA01")
                .append(ByteUtil.decimal2fitHex(water, 8))
                .append("01")
                .append(ByteUtil.decimal2fitHex(weight, 8))
                .append("01")
                .append(ByteUtil.decimal2fitHex(precision));

        Log.e("lxk", "精度" + ByteUtil.decimal2fitHex(precision));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(0, sb.toString());
    }

    public void addCA05(int water, int zw) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("000C09CA0500").append(ByteUtil.decimal2fitHex(water, 8)).append(ByteUtil.decimal2fitHex(zw, 8));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(0, sb.toString());
    }
    //设置碾米精度
    public void addC81(int grade) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00030C81").append(ByteUtil.decimal2fitHex(grade, 2));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        LogPlus.e("设置碾米分度命令：=======" + sb.toString());
        addCmd(1, sb.toString());
    }

    public void addC0E() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C0E");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }

    //    public void addC82() {
    //        StringBuilder sb = new StringBuilder();
    //        sb.append(stringBuilder).append("00020C82");
    //        String crc = CRC16Utils.getCRC16(sb.toString());
    //        sb.append(crc);
    //        addCmd(1, sb.toString());
    //    }
    //测试各种电机 num(电机编号) state(电机动作0关闭1打开)
    public void addC83(int num, int state) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C83").append(ByteUtil.decimal2fitHex(num, 2)).append(ByteUtil.decimal2fitHex(state, 2));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        LogPlus.e("测试电机命令：=======" + sb.toString());
        addCmd(1, sb.toString());
    }

    public void addC04() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C04");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }

    public void addC05(int num, int grade) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C05").append(ByteUtil.decimal2fitHex(num, 2)).append(ByteUtil.decimal2fitHex(grade, 2));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        LogPlus.e("读取补偿质量命令：=======" + sb.toString());
        addCmd(1, sb.toString());
    }

    public void addC85(int num, int grade, int weight) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder)
                .append("00060C85")
                .append(ByteUtil.decimal2fitHex(num, 2))
                .append(ByteUtil.decimal2fitHex(grade, 2))
                .append(ByteUtil.decimal2fitHex(weight, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        LogPlus.e("设置补偿质量命令：=======" + sb.toString());
        addCmd(1, sb.toString());
    }

    public void addC07() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C07");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        LogPlus.e("读碾米机电机延迟停转时间命令：=======" + sb.toString());
        addCmd(1, sb.toString());
    }

    public void addC87(int time) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C87").append(ByteUtil.decimal2fitHex(time, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        LogPlus.e("设置碾米机电机延迟停转时间命令：=======" + sb.toString());
        addCmd(1, sb.toString());
    }

    public void addC08() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C08");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }

    public void addC88(int time) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C88").append(ByteUtil.decimal2fitHex(time, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }

    public void addC09(int grade) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00030C09").append(ByteUtil.decimal2fitHex(grade, 2));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    public void addC09() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C09");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }

    public void addC89(int grade, int time) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C89").append(ByteUtil.decimal2fitHex(time, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }

    public void addC8D(int time) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C8D").append(ByteUtil.decimal2fitHex(time, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }

    public void addC8C(int time) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00030C8C").append(ByteUtil.decimal2fitHex(time, 2));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    public void addC0C() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C0C");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    public void addC11() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C11");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    public void addC12(int time) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C12").append(ByteUtil.decimal2fitHex(time, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        LogPlus.e("设置风扇温度命令："+sb.toString());
        addCmd(1, sb.toString());
    }
    public void addC13(int mode,int type) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C13").append(ByteUtil.decimal2fitHex(mode, 2)).append(ByteUtil.decimal2fitHex(type, 2));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        LogPlus.e("C13命令："+sb.toString());
        addCmd(1, sb.toString());
    }
    public void addC14(int mode,int type,int temp) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00060C14").append(ByteUtil.decimal2fitHex(mode, 2)).append(ByteUtil.decimal2fitHex(type, 2)).append(ByteUtil.decimal2fitHex(temp, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        LogPlus.e("C14命令："+sb.toString());
        addCmd(1, sb.toString());
    }
    //读温度超温报警设定温度0C16
    public void addC16(){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C16");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //设置温度超温报警设定温度0C17
    public void addC17(int temp){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C17").append(ByteUtil.decimal2fitHex(temp, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //读加热超时报警时间0C18
    public void addC18(){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C18");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //设置加热超时报警时间0C19
    public void addC19(int temp){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C19").append(ByteUtil.decimal2fitHex(temp, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //读下米超时报警时间0C1A
    public void addC1A(){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C1A");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //设置下米超时报警时间0C1B
    public void addC1B(int temp){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C1B").append(ByteUtil.decimal2fitHex(temp, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //读拉膜超时报警时间0C1C
    public void addC1C(){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C1C");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //设置拉膜超时报警时间0C1D
    public void addC1D(int temp){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C1D").append(ByteUtil.decimal2fitHex(temp, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //读封膜超时报警时间0C1E
    public void addC1E(){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C1E");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //设置封膜超时报警时间0C1F
    public void addC1F(int temp){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C1F").append(ByteUtil.decimal2fitHex(temp, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //读送谷超时报警时间0C20
    public void addC20(){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C20");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //设置送谷超时报警时间0C21
    public void addC21(int temp){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C21").append(ByteUtil.decimal2fitHex(temp, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //读送膜超时报警时间0C22
    public void addC22(){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C22");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //设置送膜超时报警时间0C23
    public void addC23(int temp){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00040C23").append(ByteUtil.decimal2fitHex(temp, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }
    //解除提升机超时报警0C15
    public void addC15(){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C15");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }

    public void addC01(){
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C01");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }


    public void addC82(int i, int weight) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00050C82").append(ByteUtil.decimal2fitHex(i, 2)).append(ByteUtil.decimal2fitHex(weight, 4));
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }

    public void addC0D() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringBuilder).append("00020C0D");
        String crc = CRC16Utils.getCRC16(sb.toString());
        sb.append(crc);
        addCmd(1, sb.toString());
    }

    private void addCmd(int index, String s) {
        if (index == 0) {
            cmdPacks.add(0, s);
        } else {
            cmdPacks.add(s);
        }
    }

    private void startTimer() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (cmdPacks.size() > 0) {
                        if (cmdPacks.contains(lastPackHexStr)) {
                            if (timeOut > maxTime) {
                                if (mResult != null) {
                                    mResult.sendErrorListener(lastPackHexStr);
                                }
                                if (checkResult != null) {
                                    checkResult.sendErrorListener(lastPackHexStr);
                                }
                                if (alarmResult != null){
                                    alarmResult.sendErrorListener(lastPackHexStr);
                                }
                                cmdPacks.remove(lastPackHexStr);
                                lastPackHexStr = "";
                                timeOut = 0;
                            } else {
                                timeOut++;
                            }
                        } else {
                            if (timeOut > 0) {
                                sendCmd();
                            } else {
                                timeOut++;
                            }
                        }
                    }
                });
    }

    private void sendCmd() {
        if (!cmdPacks.isEmpty() && !cmdPacks.contains(lastPackHexStr)) {
            try {
                if (isOpenSuccess()) {
                    lastPackHexStr = cmdPacks.get(0);
                    timeOut = 0;
                    LogPlus.e("发送" + lastPackHexStr);
                    try {
                        manager.sendData(lastPackHexStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (lastPackHexStr.substring(20, 26).equals("09CA05")) {
                        cmdPacks.remove(lastPackHexStr);
                        timeOut = 0;
                        lastPackHexStr = "";
                    }
                } else {
                    checkResult.sendErrorListener(lastPackHexStr);
                    alarmResult.sendErrorListener(lastPackHexStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setOperationResult(operationResult result) {
        mResult = result;
    }

    public void setCheckResult(SerialPortUtil.checkResult checkResult) {
        this.checkResult = checkResult;
    }

    public void setAlarmResult(alarmResult alarmResult){
        this.alarmResult = alarmResult;
    }

    public interface operationResult {
        void receiveResult(String data);

        void sendErrorListener(String sendCmd);

        void openFail();
    }

    public interface checkResult {
        void receiveResult(String data);

        void sendErrorListener(String sendCmd);
    }

    public interface alarmResult{
        void receiveResult(String data);

        void sendErrorListener(String sendCmd);
    }

    public void close() {
        if (manager != null) {
            manager.close();
        }
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
