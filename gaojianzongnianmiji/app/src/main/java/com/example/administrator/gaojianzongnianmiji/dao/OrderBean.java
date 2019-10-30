package com.example.administrator.gaojianzongnianmiji.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;
/**
 * Created by lixukang   on  2019/7/17.
 */

@Entity
public class OrderBean {

    @Id
    private Long id;

    @Index(unique = true)
    private String orderNumber;

    private int weight;

    private int realWeight; //实际出米重量

    private long updateTime;

    private int updateState; //1上报成功 2上报失败

    private int successState;//1启动成功 2碾米完成 3失败 看failState

    private int failState;//1超时结束 3 机器故障 4发送启动命令无回复 7正在交易 8 谷仓稻谷余量不足 9 机械故障 10 重量为空 11 硬件未知异常

    private String msg;

    private int flowingWater;

    @Generated(hash = 588366752)
    public OrderBean(Long id, String orderNumber, int weight, int realWeight, long updateTime,
            int updateState, int successState, int failState, String msg, int flowingWater) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.weight = weight;
        this.realWeight = realWeight;
        this.updateTime = updateTime;
        this.updateState = updateState;
        this.successState = successState;
        this.failState = failState;
        this.msg = msg;
        this.flowingWater = flowingWater;
    }

    @Generated(hash = 1725534308)
    public OrderBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getRealWeight() {
        return this.realWeight;
    }

    public void setRealWeight(int realWeight) {
        this.realWeight = realWeight;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getUpdateState() {
        return this.updateState;
    }

    public void setUpdateState(int updateState) {
        this.updateState = updateState;
    }

    public int getSuccessState() {
        return this.successState;
    }

    public void setSuccessState(int successState) {
        this.successState = successState;
    }

    public int getFailState() {
        return this.failState;
    }

    public void setFailState(int failState) {
        this.failState = failState;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getFlowingWater() {
        return this.flowingWater;
    }

    public void setFlowingWater(int flowingWater) {
        this.flowingWater = flowingWater;
    }
}
