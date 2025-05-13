package com.example.crabfood.model;

import androidx.annotation.ColorRes;

import com.example.crabfood.model.enums.MenuAction;

public class MenuItemProfile {
    private int iconResId;
    private String title;
    private MenuAction action;
    private String selectedContent;
    private @ColorRes int iconColorRes;

    public MenuItemProfile(int iconResId, String title,
                           MenuAction action) {
        this.iconResId = iconResId;
        this.title = title;
        this.action = action;
    }

    public MenuItemProfile(int iconResId, String title,
                           MenuAction action, int iconColorRes, String selectedContent) {
        this.iconResId = iconResId;
        this.title = title;
        this.action = action;
        this.iconColorRes = iconColorRes;
        this.selectedContent = selectedContent;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    public MenuAction getAction() {
        return action;
    }

    public int getIconColorRes() {
        return iconColorRes;
    }

    public String getSelectedContent() { return selectedContent; }

    public void setSelectedContent(String selectedContent) {
        this.selectedContent = selectedContent;
    }
}
