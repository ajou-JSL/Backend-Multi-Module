package jsl.moum.backendmodule.auth.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
