package at.fhv.ec.javafxclient.view.forms;

import java.util.UUID;

public class ShoppingCartForm {
    private UUID productId;
    private String productName;
    private String artistName;
    private UUID soundCarrierId;
    private String soundCarrierName;
    private float pricePerCarrier;
    private int selectedAmount;
    private float totalProductPrice;
    private int amountAvailable;

    public ShoppingCartForm() {
    }

    public ShoppingCartForm(UUID productId, String productName, String artistName, UUID soundCarrierId,
                            String soundCarrierName, float pricePerCarrier, int selectedAmount,
                            float totalProductPrice, int amountAvailable) {
        this.productId = productId;
        this.productName = productName;
        this.artistName = artistName;
        this.soundCarrierId = soundCarrierId;
        this.soundCarrierName = soundCarrierName;
        this.pricePerCarrier = pricePerCarrier;
        this.selectedAmount = selectedAmount;
        this.totalProductPrice = totalProductPrice;
        this.amountAvailable = amountAvailable;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
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

    public float getPricePerCarrier() {
        return pricePerCarrier;
    }

    public void setPricePerCarrier(float pricePerCarrier) {
        this.pricePerCarrier = pricePerCarrier;
    }

    public int getSelectedAmount() {
        return selectedAmount;
    }

    public void setSelectedAmount(int selectedAmount) {
        this.selectedAmount = selectedAmount;
    }

    public float getTotalProductPrice() {
        return totalProductPrice;
    }

    public void setTotalProductPrice(float totalProductPrice) {
        this.totalProductPrice = totalProductPrice;
    }

    public int getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(int amountAvailable) {
        this.amountAvailable = amountAvailable;
    }
}
