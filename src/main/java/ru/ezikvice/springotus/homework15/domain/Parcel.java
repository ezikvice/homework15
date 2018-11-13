package ru.ezikvice.springotus.homework15.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parcel {

    private double weight;
    private int number;
    private String description;
    private boolean urgent;
}
