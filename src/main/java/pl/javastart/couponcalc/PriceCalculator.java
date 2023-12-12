package pl.javastart.couponcalc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PriceCalculator {

    public double calculatePrice(List<Product> products, List<Coupon> coupons) {
        double result;

        if (products == null) {
            result = 0;
        } else if (coupons == null) {
            result = calculateTotalPriceWithNoDiscounts(products);
        } else if (coupons.size() == 1) {
            result = calculateTotalPriceWithOneCoupon(products, coupons.get(0));
        } else {
            result = calculateTotalPriceWithTwoOrMoreCoupons(products, coupons);
        }

        return truncate(result);
    }

    private Double truncate(double arg) {
        return BigDecimal.valueOf(arg).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /*
    Poniższa metoda zestawia wszystkie istniejące kupony z potencjalnymi łącznymi kwotami zniżek, które każdy z tych kuponów
    mógłby zapewnić. Dzięki temu będzie później możliwy wybór takiego kuponu, który daje największą sumę zniżek, zgodnie z treścią
    zadania.
     */
    private Map<Coupon, Double> groupCouponsByDiscountAmount(List<Product> products, List<Coupon> coupons) {
        Map<Coupon, Double> couponsGroupedByTotalDiscountAmount = new HashMap<>();

        for (Coupon coupon : coupons) {
            Category category = coupon.getCategory();
            couponsGroupedByTotalDiscountAmount.put(coupon,
                    products.stream()
                            .filter(product -> product.getCategory().equals(category))
                            .map(Product::getPrice)
                            .map(price -> price * ((double) coupon.getDiscountValueInPercents() / 100))
                            .reduce(0.0, Double::sum));
        }

        return couponsGroupedByTotalDiscountAmount;
    }

    /*
    Poniższa metoda znajduje kupon (a tym samym i kategorię), który daje największą sumę zniżek zgodnie z treścią zadania.
     */
    private Coupon findHighestDiscountAmountCoupon(Map<Coupon, Double> couponsGroupedByDiscountAmount) {
        Coupon highestDiscountCoupon = null;
        double highestDiscount = 0;

        for (Map.Entry<Coupon, Double> couponEntry : couponsGroupedByDiscountAmount.entrySet()) {
            if (couponEntry.getValue() > highestDiscount) {
                highestDiscountCoupon = couponEntry.getKey();
                highestDiscount = couponEntry.getValue();
            }
        }

        return highestDiscountCoupon;
    }

    private double calculateDiscountValueInDouble(int discountValueInPercents) {
        return 1 - ((double) discountValueInPercents / 100);
    }

    /*
    To grupowanie poniżej zastosowałem, by ułatwić sobie wyliczenie łącznych kosztów produktów, zarówno tych ze zniżką jak i bez.
     */
    private double calculateTotalPriceWithSpecificCategoryDiscount(List<Product> products, Coupon coupon) {
        Category category = coupon.getCategory();
        Map<Boolean, List<Product>> productsGroupedByOneCategory = products.stream()
                .collect(Collectors.partitioningBy(product -> product.getCategory().equals(category)));

        List<Product> productsWithGivenCoupon = productsGroupedByOneCategory.get(true);
        List<Product> productsWithoutGivenCoupon = productsGroupedByOneCategory.get(false);

        double totalPriceOfProductsWithGivenCoupon = productsWithGivenCoupon.stream()
                .map(Product::getPrice)
                .map(price -> price * calculateDiscountValueInDouble(coupon.getDiscountValueInPercents()))
                .reduce(0.0, Double::sum);

        double totalPriceOfProductsWithoutGivenCoupon = productsWithoutGivenCoupon.stream()
                .map(Product::getPrice)
                .reduce(0.0, Double::sum);

        return totalPriceOfProductsWithGivenCoupon + totalPriceOfProductsWithoutGivenCoupon;
    }

    private double calculateTotalPriceWithTwoOrMoreCoupons(List<Product> products, List<Coupon> coupons) {
        //Kupony z kategorią null też powinny się załapać, jako że HashMapa dopuszcza istnienie jednego entry z nullowym kluczem
        Map<Coupon, Double> couponsGroupedByDiscountAmount = groupCouponsByDiscountAmount(products, coupons);
        Coupon highestDiscountAmountCoupon = findHighestDiscountAmountCoupon(couponsGroupedByDiscountAmount);
        return calculateTotalPriceWithSpecificCategoryDiscount(products, highestDiscountAmountCoupon);
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