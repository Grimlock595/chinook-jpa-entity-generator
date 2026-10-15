package generated_entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "invoice_line")
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_line_id", nullable = false)
    @NotNull
    private Integer invoiceLineId;

    @Column(name = "invoice_id", nullable = false)
    @NotNull
    private Integer invoiceId;

    @Column(name = "track_id", nullable = false)
    @NotNull
    private Integer trackId;

    @Column(name = "unit_price", nullable = false)
    @NotNull
    private java.math.BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    @NotNull
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", referencedColumnName = "invoice_id", insertable = false, updatable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", referencedColumnName = "track_id", insertable = false, updatable = false)
    private Track track;

    public Integer getInvoiceLineId() {
        return invoiceLineId;
    }

    public void setInvoiceLineId(Integer invoiceLineId) {
        this.invoiceLineId = invoiceLineId;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public java.math.BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(java.math.BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

}
