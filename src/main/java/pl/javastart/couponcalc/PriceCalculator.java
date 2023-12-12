package pl.javastart.couponcalc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PriceCalculator {

    public double calculatePrice(List<Product> products, List<Coupon> coupons) {
        double result;

        if (products == null) {
            result = 0;
        } else if (coupons == null) {
            result = calculateTotalPriceWithNoDiscounts(products);
        } else {
            result = calculateTotalPriceWithTwoOrMoreCoupons(products, coupons);
        }

        return truncate(result);
    }

    private Double calculatePrice(Product product, Coupon coupon) {
        if (product.getCategory() == coupon.getCategory()) {
            return product.getPrice() * calculateDiscountValueInDouble(coupon.getDiscountValueInPercents());
        }
        return product.getPrice();
    }

    private Double truncate(double arg) {
        return BigDecimal.valueOf(arg).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private double calculateDiscountValueInDouble(int discountValueInPercents) {
        return 1 - ((double) discountValueInPercents / 100);
    }

    private double calculateTotalPriceWithSpecificCategoryDiscount(List<Product> products, Coupon coupon) {
        return products.stream()
                .map(product -> calculatePrice(product, coupon))
                .reduce(0.0, Double::sum);
    }

    private double calculateTotalPriceWithTwoOrMoreCoupons(List<Product> products, List<Coupon> coupons) {
        double bestPrice = Double.MAX_VALUE;

        for (Coupon coupon : coupons) {
            double price = calculateTotalPriceWithOneCoupon(products, coupon);
            bestPrice = Math.min(bestPrice, price);
        }

        return bestPrice;
    }

    private double calculateTotalPriceWithNoDiscounts(List<Product> products) {
        return products.stream()
                .map(Product::getPrice)
                .reduce(0.0, Double::sum);
    }

    private double calculateTotalPriceWithOneCoupon(List<Product> products, Coupon coupon) {
        if (coupon.getCategory() == null) {
            return calculateTotalPriceWithNoDiscounts(products)
                    * calculateDiscountValueInDouble(coupon.getDiscountValueInPercents());
        } else {
            return calculateTotalPriceWithSpecificCategoryDiscount(products, coupon);
        }
    }
}