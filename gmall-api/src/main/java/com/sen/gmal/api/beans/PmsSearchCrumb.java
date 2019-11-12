package com.sen.gmal.api.beans;

import java.io.Serializable;

/**
 * @Auther: Sen
 * @Date: 2019/11/7 02:33
 * @Description:
 */
public class PmsSearchCrumb implements Serializable {

    private static final long serialVersionUID = -3369354839081481213L;

    private String valueName;

    private String urlParam;

    private String valueId;

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }
}
