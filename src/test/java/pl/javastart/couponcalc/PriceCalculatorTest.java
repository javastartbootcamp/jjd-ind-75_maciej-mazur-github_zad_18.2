package pl.javastart.couponcalc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PriceCalculatorTest {
    private final PriceCalculator priceCalculator = new PriceCalculator();

    @Test
    public void shouldReturnZeroForNoProducts() {

        // when
        double result = priceCalculator.calculatePrice(null, null);

        // then
        assertThat(result).isEqualTo(0.);
    }

    @Test
    public void shouldReturnPriceForSingleProductAndNoCoupons() {

        // given
        List<Product> products = new ArrayList<>();
        products.add(new Product("Masło", 5.99, Category.FOOD));

        // when
        double result = priceCalculator.calculatePrice(products, null);

        // then
        assertThat(result).isEqualTo(5.99);
    }

    @Test
    void shouldReturnTotalBasePriceSumWithNoDiscountsForMultipleProductsAndNoCoupons() {
        //given
        List<Product> products = List.of(
                new Product("Szafa", 1900.50, Category.HOME),
                new Product("Sony PlayStation 4", 1500.99, Category.ENTERTAINMENT),
                new Product("Honda Civic", 100000, Category.CAR)
        );

        //when
        double result = priceCalculator.calculatePrice(products, null);

        //then
        assertThat(result).isEqualTo(103401.49);
    }

    @Test
    void shouldReturnTotalBasePriceSumReducedByDiscountForAllProductsWhenOneNullCategoryCouponIsUsed() {
        //given
        List<Product> products = List.of(
                new Product("Szafa", 1900.50, Category.HOME),
                new Product("Sony PlayStation 4", 1500.99, Category.ENTERTAINMENT),
                new Product("Honda Civic", 100000, Category.CAR)
        );
        List<Coupon> coupons = List.of(new Coupon(null, 15));

        //when
        double result = priceCalculator.calculatePrice(products, coupons);

        //then
        assertThat(result).isEqualTo(87891.27);
    }

    @Test
    void shouldApplyDiscountForSameCategoryProductsOnlyWhenOneSpecificCategoryCouponIsUsed() {
        //given
        List<Product> products = List.of(
                new Product("Masło", 5.99, Category.FOOD),
                new Product("Kebab", 30.45, Category.FOOD),
                new Product("Pizza", 45.79, Category.FOOD),
                new Product("Sony PlayStation 4", 1500.99, Category.ENTERTAINMENT),
                new Product("Honda Civic", 100000, Category.CAR)
        );
        List<Coupon> coupons = List.of(new Coupon(Category.FOOD, 10));

        //when
        double result = priceCalculator.calculatePrice(products, coupons);

        //then
        assertThat(result).isEqualTo(101575);
    }

    @Test
    void shouldApplyMostAdvantageousDiscountWhenTwoOrMoreCouponsAreUsed() {
        //given
        List<Product> products = List.of(
                new Product("Masło", 5.99, Category.FOOD),
                new Product("Kebab", 30.45, Category.FOOD),
                new Product("Pizza", 45.79, Category.FOOD),
                new Product("Sony PlayStation 4", 1500.99, Category.ENTERTAINMENT),
                new Product("Honda Civic", 100000, Category.CAR)
        );
        List<Coupon> coupons = List.of(
                new Coupon(Category.FOOD, 50),
                new Coupon(null, 1),
                new Coupon(Category.CAR, 10));

        //when
        double result = priceCalculator.calculatePrice(products, coupons);

        //then
        assertThat(result).isEqualTo(91583.22);
    }

    @Test
    public void shouldReturnPriceForSingleProductAndOneCoupon() {

        // given
        List<Product> products = new ArrayList<>();
        products.add(new Product("Masło", 5.99, Category.FOOD));

        List<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon(Category.FOOD, 20));

        // when
        double result = priceCalculator.calculatePrice(products, coupons);

        // then
        assertThat(result).isEqualTo(4.79);
    }


}