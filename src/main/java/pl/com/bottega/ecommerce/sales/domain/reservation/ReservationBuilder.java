package pl.com.bottega.ecommerce.sales.domain.reservation;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import java.util.ArrayList;
import java.util.Date;

public class ReservationBuilder {

    private Id id = Id.generate();
    private Reservation.ReservationStatus status = Reservation.ReservationStatus.OPENED;
    private ClientData clientData;
    private Date createDate = new Date();
    private ArrayList<ReservationItem> items;

    public ReservationBuilder() {
    }

    public ReservationBuilder withAggregateId(Id aggregateId) {
        this.id = aggregateId;
        return this;
    }

    public ReservationBuilder withStatus(Reservation.ReservationStatus status) {
        this.status = status;
        return this;
    }

    public ReservationBuilder withClientData(ClientData clientData) {
        this.clientData = clientData;
        return this;
    }

    public ReservationBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Reservation build() {
        return new Reservation(id, status, clientData, createDate);
    }
}
