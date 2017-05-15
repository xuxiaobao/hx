package com.miaosu.service.serialno;

import java.io.Serializable;

/**
 * 序列号
 */
public class SerialNo implements Serializable{
    private String seqName;

    private Long curlVal;

    private Long incrementVal;

    public String getSeqName() {
        return seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    public Long getCurlVal() {
        return curlVal;
    }

    public void setCurlVal(Long curlVal) {
        this.curlVal = curlVal;
    }

    public Long getIncrementVal() {
        return incrementVal;
    }

    public void setIncrementVal(Long incrementVal) {
        this.incrementVal = incrementVal;
    }
}
