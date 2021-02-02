package com.brunotarditi.compra_facil.models;


import com.brunotarditi.compra_facil.app.MyApplication;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Buy extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String title;
    @Required
    private Date createdAt;
    private RealmList<Product> products;

    public Buy() {
    }

    public Buy(String title) {
        this.id = MyApplication.boardID.incrementAndGet();
        this.title = title;
        this.createdAt = new Date();
        this.products = new RealmList<Product>();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public RealmList<Product> getProducts() {
        return products;
    }

    public double totalProducts(){
        double total = 0;
        if (this.getProducts() != null){
            for (Product p : this.getProducts()) {
                total += p.total();
            }
        }
        return  total;
    }

}
