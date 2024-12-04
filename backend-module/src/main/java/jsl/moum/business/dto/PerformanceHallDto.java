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
    private String address;
    private String owner;
    private String phone;
    private String email;
    private String mapUrl;
    private List<String> imageUrls;
    private int price;
    private int size;
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
        private int size;
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

        public PerformanceHall toEntity(){
            return PerformanceHall.builder()
                    .name(name)
                    .price(price)
                    .size(size)
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
    public static class Images {
        private List<String> imageUrls;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private int id;
        private String name;
        private String address;
        private String owner;
        private String phone;
        private String email;
        private String mapUrl;
        private List<String> imageUrls;
        private int price;
        private int size;
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

        public Response(PerformanceHall hall){
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

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private String name;
        private String address;
        private String owner;
        private String phone;
        private String email;
        private int price;
        private int size;
        private int capacity;
        private int type;
        private int stand;
        private boolean hasPiano;
        private boolean hasAmp;
        private boolean hasSpeaker;
        private boolean hasMic;
        private boolean hasDrums;
        private String details;

        public PerformanceHall toEntity(PerformanceHall hall){
            hall.setName(name);
            hall.setPrice(price);
            hall.setSize(size);
            hall.setCapacity(capacity);
            hall.setAddress(address);
            hall.setOwner(owner);
            hall.setPhone(phone);
            hall.setEmail(email);
            hall.setType(type);
            hall.setStand(stand);
            hall.setHasPiano(hasPiano);
            hall.setHasAmp(hasAmp);
            hall.setHasSpeaker(hasSpeaker);
            hall.setHasMic(hasMic);
            hall.setHasDrums(hasDrums);
            hall.setDetails(details);
            return hall;
        }
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Search {
        private String name;
        private Double latitude;
        private Double longitude;
        private Integer minPrice;
        private Integer maxPrice;
        private Integer minSize;
        private Integer maxSize;
        private Integer minCapacity;
        private Integer maxCapacity;
        private Integer minStand;
        private Integer maxStand;
        private Boolean hasPiano;
        private Boolean hasAmp;
        private Boolean hasSpeaker;
        private Boolean hasMic;
        private Boolean hasDrums;
    }
}
