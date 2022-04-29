package at.fhv.ec.javafxclient.model;

import java.util.Objects;
import java.util.UUID;

public class ShoppingCartEntry {
    private UUID productId;
    private String productName;
    private String artistName;
    private UUID soundCarrierId;
    private String soundCarrierName;
    private float pricePerCarrier;
    private int selectedAmount;
    private float totalProductPrice;
    private int amountAvailable;

    public ShoppingCartEntry(UUID productId, String productName, String artistName, UUID soundCarrierId,
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

    public float getPricePerCarrier() {
        return pricePerCarrier;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartEntry that = (ShoppingCartEntry) o;
        return Objects.equals(soundCarrierId, that.soundCarrierId);
    }

    @Override
    public int hashCode() {
        // Only soundCarrierId because it should not be possible to add same sound carrier twice to shopping cart
        return Objects.hash(soundCarrierId);
    }
}
