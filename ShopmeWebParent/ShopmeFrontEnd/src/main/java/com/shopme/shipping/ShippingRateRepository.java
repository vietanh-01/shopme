package com.shopme.shipping;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

    ShippingRate findByCountryAndState(Country country, String state);
}
