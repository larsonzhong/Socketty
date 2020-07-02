package com.skyruler.middleware.parser;

import com.skyruler.middleware.parser.xml.model.SubItem;

import java.util.ArrayList;
import java.util.List;

public class BaseStation {
    protected byte mSid;
    protected String mName;
    protected List<SubItem> mSubItems = new ArrayList<>();

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public byte getSid() {
        return mSid;
    }

    public void setSid(byte sid) {
        this.mSid = sid;
    }

    public List<SubItem> getSubItems() {
        return mSubItems;
    }

    public void setSubItems(List<SubItem> subItems) {
        this.mSubItems = subItems;
    }

}
