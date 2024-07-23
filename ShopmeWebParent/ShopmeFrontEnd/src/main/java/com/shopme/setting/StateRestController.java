package com.shopme.setting;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import com.shopme.common.entity.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StateRestController {

    @Autowired
    private StateRepository repo;

    @GetMapping("/settings/list_states_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Integer countyId) {
        List<State> stateList = repo.findByCountryOrderByNameAsc(new Country(countyId));
        List<StateDTO> result = new ArrayList<>();

        for (State state : stateList) {
            result.add(new StateDTO(state.getId(), state.getName()));
        }

        return result;
    }


}
