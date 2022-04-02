package at.fhv.ec.javafxclient.view.forms;

import java.util.UUID;

public class RefundedSaleItem {
    private String productName;
    private String artistName;
    private UUID soundCarrierId;
    private String soundCarrierName;
    private int amountOfCarriers;
    private float pricePerCarrier;
    private boolean isRefunded;
    private int amountToRefund;

    public RefundedSaleItem(String productName, String artistName, UUID soundCarrierId, String soundCarrierName, int amountOfCarriers, float pricePerCarrier, boolean isRefunded) {
        this.productName = productName;
        this.artistName = artistName;
        this.soundCarrierId = soundCarrierId;
        this.soundCarrierName = soundCarrierName;
        this.amountOfCarriers = amountOfCarriers;
        this.pricePerCarrier = pricePerCarrier;
        this.isRefunded = isRefunded;
        amountToRefund = 0;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public UUID getSoundCarrierId() {
        return soundCarrierId;
    }

    public void setSoundCarrierId(UUID soundCarrierId) {
        this.soundCarrierId = soundCarrierId;
    }

    public String getSoundCarrierName() {
        return soundCarrierName;
    }

    public void setSoundCarrierName(String soundCarrierName) {
        this.soundCarrierName = soundCarrierName;
    }

    public int getAmountOfCarriers() {
        return amountOfCarriers;
    }

    public void setAmountOfCarriers(int amountOfCarriers) {
        this.amountOfCarriers = amountOfCarriers;
    }

    public float getPricePerCarrier() {
        return pricePerCarrier;
    }

    public void setPricePerCarrier(float pricePerCarrier) {
        this.pricePerCarrier = pricePerCarrier;
    }

    public boolean isRefunded() {
        return isRefunded;
    }

    public void setRefunded(boolean refunded) {
        isRefunded = refunded;
    }

    public int getAmountToRefund() {
        return amountToRefund;
    }

    public void setAmountToRefund(int amountToRefund) {
        this.amountToRefund = amountToRefund;
    }
}
