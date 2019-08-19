package com.qhjf.cfm.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 登陆用户的菜单信息
 */
public class ModuleInfo implements Serializable {

    private static final long serialVersionUID = 2088527526972135265L;
    private String name;
    private String code;
    private List<MenuItem> items;

    public class MenuItem implements  Serializable{

        private static final long serialVersionUID = -4568259695305494477L;
        private String code;
        private String name;

        MenuItem(String code, String name){
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public ModuleInfo(String code, String name){
        this.code = code;
        this.name = name;
    }

    public void addItem(String name, String code){
        if(items == null){
            items  = new ArrayList<>();
        }
        items.add(new MenuItem(name, code));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }
}
