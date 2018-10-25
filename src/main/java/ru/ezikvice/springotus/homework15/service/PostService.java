package ru.ezikvice.springotus.homework15.service;

import org.springframework.stereotype.Service;
import ru.ezikvice.springotus.homework15.domain.Parcel;

@Service
public class PostService {
    public Parcel reportReceiving(Parcel parcel) {
        parcel.setWeight(parcel.getWeight() * 0.8);
        System.out.printf("----- parcel #%s with weight=%f received!", parcel.getNumber(), parcel.getWeight());
        return parcel;
    }
}
