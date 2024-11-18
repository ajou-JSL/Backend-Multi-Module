package jsl.moum.business.dto;

import jsl.moum.business.domain.PracticeRoom;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PracticeRoomDto {
    private int id;
    private String name;
    private int price;
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
    private Float latitude;
    private Float longitude;

    public PracticeRoomDto(PracticeRoom room){
        this.id = room.getId();
        this.name = room.getName();
        this.price = room.getPrice();
        this.capacity = room.getCapacity();
        this.address = room.getAddress();
        this.owner = room.getOwner();
        this.phone = room.getPhone();
        this.email = room.getEmail();
        this.mapUrl = room.getMapUrl();
        this.imageUrls = room.getImageUrls();
        this.type = room.getType();
        this.stand = room.getStand();
        this.hasPiano = room.isHasPiano();
        this.hasAmp = room.isHasAmp();
        this.hasSpeaker = room.isHasSpeaker();
        this.hasMic = room.isHasMic();
        this.hasDrums = room.isHasDrums();
        this.details = room.getDetails();
        this.latitude = room.getLatitude();
        this.longitude = room.getLongitude();
    }
}