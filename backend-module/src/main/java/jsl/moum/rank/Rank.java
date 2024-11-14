package jsl.moum.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Rank {
    GREEN("그린"),
    BRONZE("브론즈"),
    SILVER("실버"),
    GOLD("골드"),
    PLATINUM("플래티넘"),
    DIAMOND("다이아몬드");

    private final String tier;

    public static Rank getRank(int exp) {
        if (exp < 5) {
            return GREEN;
        } else if (exp < 10) {
            return BRONZE;
        } else if (exp < 15) {
            return SILVER;
        } else if (exp < 20) {
            return GOLD;
        } else if (exp < 25) {
            return PLATINUM;
        } else {
            return DIAMOND;
        }
    }
}
