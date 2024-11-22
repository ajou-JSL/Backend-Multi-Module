package jsl.moum.business.dto;

import jsl.moum.business.domain.PerformanceHall;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceHallDto {
    private int id;
    private String name;
    private int price;
    private int size;
    private int capacity;
    private String address;
    private String owner;
    private String phone;
    private String email;
    private String mapUrl;
    private List<String> imageUrls;
    private int type;
    private int stand;
    private boolean hasPiano;
    private boolean hasAmp;
    private boolean hasSpeaker;
    private boolean hasMic;
    private boolean hasDrums;
    private String details;
    private Double latitude;
    private Double longitude;

    public PerformanceHallDto(PerformanceHall hall){
        this.id = hall.getId();
        this.name = hall.getName();
        this.price = hall.getPrice();
        this.size = hall.getSize();
        this.capacity = hall.getCapacity();
        this.address = hall.getAddress();
        this.owner = hall.getOwner();
        this.phone = hall.getPhone();
        this.email = hall.getEmail();
        this.mapUrl = hall.getMapUrl();
        this.imageUrls = hall.getImageUrls();
        this.type = hall.getType();
        this.stand = hall.getStand();
        this.hasPiano = hall.isHasPiano();
        this.hasAmp = hall.isHasAmp();
        this.hasSpeaker = hall.isHasSpeaker();
        this.hasMic = hall.isHasMic();
        this.hasDrums = hall.isHasDrums();
        this.details = hall.getDetails();
        this.latitude = hall.getLatitude();
        this.longitude = hall.getLongitude();
    }

}
