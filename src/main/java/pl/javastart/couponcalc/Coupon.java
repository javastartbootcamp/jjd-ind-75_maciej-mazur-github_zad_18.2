package pl.javastart.couponcalc;

import java.util.Objects;

public class Coupon {

    private final Category category;
    private final int discountValueInPercents;

    public Coupon(Category category, int discountValueInPercents) {
        this.category = category;
        this.discountValueInPercents = discountValueInPercents;
    }

    public Category getCategory() {
        return category;
    }

    public int getDiscountValueInPercents() {
        return discountValueInPercents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coupon coupon = (Coupon) o;
        return getDiscountValueInPercents() == coupon.getDiscountValueInPercents() && getCategory() == coupon.getCategory();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategory(), getDiscountValueInPercents());
    }
}