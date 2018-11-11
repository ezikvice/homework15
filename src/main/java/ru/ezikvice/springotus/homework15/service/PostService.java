package ru.ezikvice.springotus.homework15.service;

import org.springframework.stereotype.Service;
import ru.ezikvice.springotus.homework15.domain.Parcel;

@Service
public class PostService {
    public Parcel reportSending(Parcel parcel) {
        System.out.printf("----- parcel #%s with weight=%f send!", parcel.getNumber(), parcel.getWeight());
        return parcel;
    }

    public Parcel acceptParcel(Parcel parcel) {
        System.out.printf("----- parcel #%s with weight=%f accepted!", parcel.getNumber(), parcel.getWeight());
        return parcel;
    }
}
