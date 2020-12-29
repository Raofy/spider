package com.jin10.spider.spiderserver.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Item{
    private int index;
    private String key;

    public Item(int index, String key) {
        this.index = index;
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Item{" +
                "index=" + index +
                ", key='" + key + '\'' +
                '}';
    }

    public static void main(String[] args) {
        String text = "【快讯】20201102递延方向及结算价 Ag(T D) 多付空 4998 Au(T D) 多付空 398.76 mAu(T D) 空付多 399.27";

        text = text.toLowerCase();


        List<Item> sorts = new ArrayList<>();

        sorts.add(new Item(text.indexOf("ag"), "ag"));
        text = text.replaceFirst("ag", "嘄嘄");

        sorts.add(new Item(text.indexOf("mau"), "mau"));
        text = text.replaceFirst("mau", "嘄嘄嘄");

        sorts.add(new Item(text.indexOf("au"), "au"));
        text = text.replaceFirst("au", "嘄嘄");


        Collections.sort(sorts, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.index - o2.index;
            }
        });

        String[] items = text.split("嘄嘄");

        for(int i = 0; i < sorts.size(); i++){
            System.out.println(sorts.get(i).key + ":" + (items[i + 1].contains("多付空")?"1":"2"));
        }


        System.out.println(sorts.toString());
    }

}
