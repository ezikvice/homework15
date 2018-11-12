package ru.ezikvice.springotus.homework15.service;

import org.springframework.stereotype.Service;
import ru.ezikvice.springotus.homework15.domain.Parcel;

import java.util.List;

@Service
public class PostService {
    public void reportSending(Parcel parcel) {
        System.out.printf("----- parcel #%s with weight=%f send!", parcel.getNumber(), parcel.getWeight());
    }

    public Parcel acceptParcel(Parcel parcel) {
        System.out.printf("+++++ parcel #%s with weight=%f accepted!", parcel.getNumber(), parcel.getWeight());
        return parcel;
    }

    public void reportReceivingList(List<Parcel> parcels) {
        System.out.println("===== Parcels received:");
        for (Parcel parcel : parcels) {
            System.out.println("\t" + parcel.getNumber() + ": " + parcel.getDescription() + " Weight: " + parcel.getWeight());
        }
    }
}
