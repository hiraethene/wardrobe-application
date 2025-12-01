package com.example.wardrobe_application;

import android.os.Parcel;
import android.os.Parcelable;

public class WardrobeItem implements Parcelable {
    public String itemImage;
    public String itemCategory;
    public String itemTitle;
    public String itemDescription;
    public String itemBrand;
    public String itemSize;
    public String itemCondition;
    public String itemColour;
    public String itemMaterial;
    public String itemPrice;

    public WardrobeItem() {

    }

    public WardrobeItem(String image,String category, String title, String description, String brand, String size, String condition, String colour, String material,String price){
        itemImage = image;
        itemCategory = category;
        itemTitle = title;
        itemDescription = description ;
        itemBrand = brand;
        itemSize = size;
        itemCondition = condition;
        itemColour = colour;
        itemMaterial = material;
        itemPrice = price;
    }

    private WardrobeItem(Parcel in) {
        itemImage = in.readString();
        itemCategory = in.readString();
        itemTitle = in.readString();
        itemDescription = in.readString();
        itemBrand = in.readString();
        itemSize = in.readString();
        itemCondition = in.readString();
        itemColour = in.readString();
        itemMaterial = in.readString();
        itemPrice = in.readString();

    }

    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<MyParcelable> CREATOR` constant for our class;
    // Notice how it has our class specified as its type.
    public static final Parcelable.Creator<WardrobeItem> CREATOR = new Creator<WardrobeItem>() {
        @Override
        public WardrobeItem createFromParcel(Parcel in) {
            return new WardrobeItem(in);
        }

        @Override
        public WardrobeItem[] newArray(int size) {
        return new WardrobeItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemImage);
        dest.writeString(itemCategory);
        dest.writeString(itemTitle);
        dest.writeString(itemDescription);
        dest.writeString(itemBrand);
        dest.writeString(itemSize);
        dest.writeString(itemCondition);
        dest.writeString(itemColour);
        dest.writeString(itemMaterial);
        dest.writeString(itemPrice);
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
