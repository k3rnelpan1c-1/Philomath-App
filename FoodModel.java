package com.example.suraj.philomath;

public class FoodModel {

    private String item_paper;
    private int item_image;
    private String item_paper_link;

    public FoodModel( String item_paper,String item_paper_link, int item_image) {
        this.item_paper = item_paper;
        this.item_paper_link=item_paper_link;
        this.item_image = item_image;
    }


    public int getItem_image() {
        return item_image;
    }

    public void setItem_image(int item_image) {
        this.item_image = item_image;
    }

    public String getItem_paper() {
        return item_paper;
    }

    public void setItem_paper(String item_paper) {
        this.item_paper = item_paper;
    }

    public String getItem_paper_link() {
        return item_paper_link;
    }

    public void setItem_paper_link(String item_paper_link) {
        this.item_paper_link = item_paper_link;
    }
}
