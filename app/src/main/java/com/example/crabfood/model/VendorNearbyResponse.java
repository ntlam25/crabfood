package com.example.crabfood.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VendorNearbyResponse implements Serializable {
    @SerializedName("content")
    private List<VendorResponse> content;

    @SerializedName("pageNumber")
    private int pageNumber;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalElements")
    private long totalElements;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("last")
    private boolean last;

    public VendorNearbyResponse() {
    }

    public VendorNearbyResponse(List<VendorResponse> content, int pageNumber, int pageSize, int totalElements, int totalPages, boolean last) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    // Getters and Setters

    public List<VendorResponse> getContent() {
        return content;
    }

    public void setContent(List<VendorResponse> content) {
        this.content = content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
