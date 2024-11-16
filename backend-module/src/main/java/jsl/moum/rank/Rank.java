package jsl.moum.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public enum Rank {
    BRONZE("브론즈"),
    SILVER("실버"),
    GOLD("골드"),
    PLATINUM("플래티넘"),
    DIAMOND("다이아몬드");

    private final String tier;

    public static Rank getRank(int exp) {
        if (exp >= 0 && exp <= 2) {
            log.info("======= getRank() exp: {}", exp);
            return BRONZE;
        } else if (exp >= 3 && exp <= 4) {
            log.info("======= getRank() exp: {}", exp);
            return SILVER;
        } else if (exp >= 5 && exp <= 6) {
            log.info("======= getRank() exp: {}", exp);
            return GOLD;
        } else if (exp >= 7) {
            log.info("======= getRank() exp: {}", exp);
            return PLATINUM;
        } else {
            log.info("======= getRank() exp: {}", exp);
            return DIAMOND;
        }
    }
}
