package com.xuecheng.messagesdk.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author itcast
 */
@TableName("mq_message_history")
public class MqMessageHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 消息类型代码
     */
    private String messageType;

    /**
     * 关联业务信息
     */
    private String businessKey1;

    /**
     * 关联业务信息
     */
    private String businessKey2;

    /**
     * 关联业务信息
     */
    private String businessKey3;

    /**
     * 执行次数
     */
    private Integer executeNum;

    /**
     * 处理状态，0:初始，1:成功，2:失败
     */
    private Integer state;

    /**
     * 回复失败时间
     */
    private LocalDateTime returnfailureDate;

    /**
     * 回复成功时间
     */
    private LocalDateTime returnsuccessDate;

    /**
     * 回复失败内容
     */
    private String returnfailureMsg;

    /**
     * 最近执行时间
     */
    private LocalDateTime executeDate;

    private String stageState1;

    private String stageState2;

    private String stageState3;

    private String stageState4;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getBusinessKey1() {
        return businessKey1;
    }

    public void setBusinessKey1(String businessKey1) {
        this.businessKey1 = businessKey1;
    }

    public String getBusinessKey2() {
        return businessKey2;
    }

    public void setBusinessKey2(String businessKey2) {
        this.businessKey2 = businessKey2;
    }

    public String getBusinessKey3() {
        return businessKey3;
    }

    public void setBusinessKey3(String businessKey3) {
        this.businessKey3 = businessKey3;
    }

    public Integer getExecuteNum() {
        return executeNum;
    }

    public void setExecuteNum(Integer executeNum) {
        this.executeNum = executeNum;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public LocalDateTime getReturnfailureDate() {
        return returnfailureDate;
    }

    public void setReturnfailureDate(LocalDateTime returnfailureDate) {
        this.returnfailureDate = returnfailureDate;
    }

    public LocalDateTime getReturnsuccessDate() {
        return returnsuccessDate;
    }

    public void setReturnsuccessDate(LocalDateTime returnsuccessDate) {
        this.returnsuccessDate = returnsuccessDate;
    }

    public String getReturnfailureMsg() {
        return returnfailureMsg;
    }

    public void setReturnfailureMsg(String returnfailureMsg) {
        this.returnfailureMsg = returnfailureMsg;
    }

    public LocalDateTime getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(LocalDateTime executeDate) {
        this.executeDate = executeDate;
    }

    public String getStageState1() {
        return stageState1;
    }

    public void setStageState1(String stageState1) {
        this.stageState1 = stageState1;
    }

    public String getStageState2() {
        return stageState2;
    }

    public void setStageState2(String stageState2) {
        this.stageState2 = stageState2;
    }

    public String getStageState3() {
        return stageState3;
    }

    public void setStageState3(String stageState3) {
        this.stageState3 = stageState3;
    }

    public String getStageState4() {
        return stageState4;
    }

    public void setStageState4(String stageState4) {
        this.stageState4 = stageState4;
    }
}
