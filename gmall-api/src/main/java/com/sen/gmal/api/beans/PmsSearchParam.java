package com.sen.gmal.api.beans;

import java.io.Serializable;

/**
 * @Auther: Sen
 * @Date: 2019/11/6 18:38
 * @Description:
 */
public class PmsSearchParam implements Serializable {
    private static final long serialVersionUID = 7256842772505789804L;

    private String keyword;

    private String catalog3Id;

    private String[] valueId;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String[] getValueId() {
        return valueId;
    }

    public void setValueId(String[] valueId) {
        this.valueId = valueId;
    }
}
