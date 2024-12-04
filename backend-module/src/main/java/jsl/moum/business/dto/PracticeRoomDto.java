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
    private String address;
    private String owner;
    private String phone;
    private String email;
    private String mapUrl;
    private List<String> imageUrls;
    private int price;
    private int capacity;
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

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {
        private String name;
        private String address;
        private String owner;
        private String phone;
        private String email;
        private String mapUrl;
        private int price;
        private int capacity;
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

        public PracticeRoom toEntity(){
            return PracticeRoom.builder()
                    .name(name)
                    .price(price)
                    .capacity(capacity)
                    .address(address)
                    .owner(owner)
                    .phone(phone)
                    .email(email)
                    .mapUrl(mapUrl)
                    .type(type)
                    .stand(stand)
                    .hasPiano(hasPiano)
                    .hasAmp(hasAmp)
                    .hasSpeaker(hasSpeaker)
                    .hasMic(hasMic)
                    .hasDrums(hasDrums)
                    .details(details)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
        }
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Images{
        private List<String> imageUrls;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private int id;
        private String name;
        private String address;
        private String owner;
        private String phone;
        private String email;
        private String mapUrl;
        private List<String> imageUrls;
        private int price;
        private int capacity;
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

        public Response(PracticeRoom room){
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

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Search{
        private String name;
        private Double latitude;
        private Double longitude;
        private Integer minPrice;
        private Integer maxPrice;
        private Integer minCapacity;
        private Integer maxCapacity;
        private Integer type;
        private Integer minStand;
        private Integer maxStand;
        private Boolean hasPiano;
        private Boolean hasAmp;
        private Boolean hasSpeaker;
        private Boolean hasMic;
        private Boolean hasDrums;

    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update{
        private String name;
        private String address;
        private String owner;
        private String phone;
        private String email;
        private int price;
        private int capacity;
        private int type;
        private int stand;
        private boolean hasPiano;
        private boolean hasAmp;
        private boolean hasSpeaker;
        private boolean hasMic;
        private boolean hasDrums;
        private String details;
    }
}
