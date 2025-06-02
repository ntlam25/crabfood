package com.example.crabfood.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderFoodChoice implements Parcelable {
    private Long id;
    private Long orderFoodId;
    private Long optionId;
    private String optionName;
    private Long choiceId;
    private String choiceName;
    private double priceAdjustment;

    // Constructors
    public OrderFoodChoice() {}

    protected OrderFoodChoice(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            orderFoodId = null;
        } else {
            orderFoodId = in.readLong();
        }
        if (in.readByte() == 0) {
            optionId = null;
        } else {
            optionId = in.readLong();
        }
        optionName = in.readString();
        if (in.readByte() == 0) {
            choiceId = null;
        } else {
            choiceId = in.readLong();
        }
        choiceName = in.readString();
        priceAdjustment = in.readDouble();
    }

    public static final Creator<OrderFoodChoice> CREATOR = new Creator<OrderFoodChoice>() {
        @Override
        public OrderFoodChoice createFromParcel(Parcel in) {
            return new OrderFoodChoice(in);
        }

        @Override
        public OrderFoodChoice[] newArray(int size) {
            return new OrderFoodChoice[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }

        if (orderFoodId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(orderFoodId);
        }

        if (optionId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(optionId);
        }

        dest.writeString(optionName);

        if (choiceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(choiceId);
        }

        dest.writeString(choiceName);
        dest.writeDouble(priceAdjustment);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getOrderFoodId() { return orderFoodId; }

    public void setOrderFoodId(Long orderFoodId) { this.orderFoodId = orderFoodId; }

    public Long getOptionId() { return optionId; }

    public void setOptionId(Long optionId) { this.optionId = optionId; }

    public String getOptionName() { return optionName; }

    public void setOptionName(String optionName) { this.optionName = optionName; }

    public Long getChoiceId() { return choiceId; }

    public void setChoiceId(Long choiceId) { this.choiceId = choiceId; }

    public String getChoiceName() { return choiceName; }

    public void setChoiceName(String choiceName) { this.choiceName = choiceName; }

    public double getPriceAdjustment() { return priceAdjustment; }

    public void setPriceAdjustment(double priceAdjustment) { this.priceAdjustment = priceAdjustment; }
}
