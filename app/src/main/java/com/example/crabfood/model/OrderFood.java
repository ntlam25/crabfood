package com.example.crabfood.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderFood implements Parcelable {
    private Long id;
    private Long orderId;
    private Long foodId;
    private String foodName;
    private int quantity;
    private double foodPrice;
    private String foodImageUrl;
    private Date createdAt;
    private List<OrderFoodChoice> choices = new ArrayList<>();

    public OrderFood() {
        this.quantity = 1;
    }

    protected OrderFood(Parcel in) {
        id = in.readByte() == 0 ? null : in.readLong();
        orderId = in.readByte() == 0 ? null : in.readLong();
        foodId = in.readByte() == 0 ? null : in.readLong();
        foodName = in.readString();
        quantity = in.readInt();
        foodPrice = in.readDouble();
        foodImageUrl = in.readString();
        long tmpDate = in.readLong();
        createdAt = tmpDate == -1 ? null : new Date(tmpDate);
        choices = in.createTypedArrayList(OrderFoodChoice.CREATOR);
    }

    public static final Creator<OrderFood> CREATOR = new Creator<OrderFood>() {
        @Override
        public OrderFood createFromParcel(Parcel in) {
            return new OrderFood(in);
        }

        @Override
        public OrderFood[] newArray(int size) {
            return new OrderFood[size];
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

        if (orderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(orderId);
        }

        if (foodId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(foodId);
        }

        dest.writeString(foodName);
        dest.writeInt(quantity);
        dest.writeDouble(foodPrice);
        dest.writeString(foodImageUrl);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeTypedList(choices);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters (không thay đổi)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodImageUrl() {
        return foodImageUrl;
    }

    public void setFoodImageUrl(String foodImageUrl) {
        this.foodImageUrl = foodImageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderFoodChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<OrderFoodChoice> choices) {
        this.choices = choices;
    }

    public double getTotalPrice() {
        double choicesPrice = 0;
        for (OrderFoodChoice choice : choices) {
            choicesPrice += choice.getPriceAdjustment();
        }
        return (foodPrice + choicesPrice) * quantity;
    }

    public String getFormattedChoices() {
        if (choices == null || choices.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < choices.size(); i++) {
            OrderFoodChoice choice = choices.get(i);
            sb.append(choice.getChoiceName());

            if (choice.getPriceAdjustment() > 0) {
                sb.append(String.format(" (%,.0f đ)", choice.getPriceAdjustment()));
            }

            if (i < choices.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
