package pl.com.bottega.ecommerce.sales.domain.reservation;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import java.util.ArrayList;
import java.util.Date;

public class ReservationBuilder {

    private Id id;
    private Reservation.ReservationStatus status;
    private ClientData clientData;
    private Date createDate;
    private ArrayList<ReservationItem> items;

    public ReservationBuilder() {
    }

    public ReservationBuilder(Id aggregateId, Reservation.ReservationStatus status,
                              ClientData clientData, Date createDate) {
        this.id = aggregateId;
        this.status = status;
        this.clientData = clientData;
        this.createDate = createDate;
        this.items = new ArrayList<ReservationItem>();
    }

    public ReservationBuilder aggregateId(Id aggregateId) {
        this.id = aggregateId;
        return this;
    }

    public ReservationBuilder status(Reservation.ReservationStatus status) {
        this.status = status;
        return this;
    }

    public ReservationBuilder clientData(ClientData clientData) {
        this.clientData = clientData;
        return this;
    }

    public ReservationBuilder createDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Reservation build() {
        return new Reservation(id, status, clientData, createDate);
    }
}
