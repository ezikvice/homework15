package ru.ezikvice.springotus.homework15;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.ezikvice.springotus.homework15.domain.Parcel;

@MessagingGateway
public interface Shipping {
    @Gateway(requestChannel = "parcelsChannel")
    void sendParcel(Parcel parcel);
}
