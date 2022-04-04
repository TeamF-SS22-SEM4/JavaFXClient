package at.fhv.ec.javafxclient.view.forms;

import java.util.UUID;

public class SaleItemEntry {
    private String productName;
    private String artistName;
    private UUID soundCarrierId;
    private String soundCarrierName;
    private int amountOfCarriers;
    private float pricePerCarrier;
    private int refundedAmount;
    private int amountToRefund;

    public SaleItemEntry(String productName, String artistName, UUID soundCarrierId, String soundCarrierName, int amountOfCarriers, float pricePerCarrier, int refundedAmount) {
        this.productName = productName;
        this.artistName = artistName;
        this.soundCarrierId = soundCarrierId;
        this.soundCarrierName = soundCarrierName;
        this.amountOfCarriers = amountOfCarriers;
        this.pricePerCarrier = pricePerCarrier;
        this.refundedAmount = refundedAmount;
        amountToRefund = 0;
    }

    public String getProductName() {
        return productName;
    }

    public String getArtistName() {
        return artistName;
    }

    public UUID getSoundCarrierId() {
        return soundCarrierId;
    }

    public String getSoundCarrierName() {
        return soundCarrierName;
    }

    public int getAmountOfCarriers() {
        return amountOfCarriers;
    }

    public float getPricePerCarrier() {
        return pricePerCarrier;
    }

    public int getRefundedAmount() {
        return refundedAmount;
    }

    public int getAmountToRefund() {
        return amountToRefund;
    }

    public void setAmountToRefund(int amountToRefund) {
        this.amountToRefund = amountToRefund;
    }
}
