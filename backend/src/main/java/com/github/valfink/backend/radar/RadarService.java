package com.github.valfink.backend.radar;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.fooditem.FoodItemService;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RadarService {
    private final RadarRepository radarRepository;
    private final MongoUserService mongoUserService;
    private final FoodItemService foodItemService;

    private List<FoodItemDTOResponse> getFoodItemsForRadar(Radar radar) {
        return foodItemService
                .getAllFoodItems()
                .stream()
                .filter(item ->
                        item.location().coordinate().latitude().subtract(radar.center().latitude()).pow(2)
                                .add(item.location().coordinate().longitude().subtract(radar.center().longitude()).pow(2))
                                .compareTo(new BigDecimal(radar.radiusInMeters()).divide(new BigDecimal("111111"), RoundingMode.HALF_EVEN))
                                <= 0
                )
                .toList();
    }

    public RadarDTOResponse addRadar(RadarDTORequest radarDTORequest, Principal principal) {
        MongoUserDTOResponse user = mongoUserService.getMongoUserDTOResponseByUsername(principal.getName());

        if (radarRepository.existsById(user.id())) {
            throw new RadarExceptionBadInputData("You can only set one radar at a time.");
        }
        if (radarDTORequest.center() == null || radarDTORequest.center().latitude() == null || radarDTORequest.center().longitude() == null) {
            throw new RadarExceptionBadInputData("Center coordinate must be set.");
        }

        Radar radar = radarRepository.save(radarDTORequest.convertToRadar(user.id()));

        return radar.convertToDTOResponse(getFoodItemsForRadar(radar));
    }

    public RadarDTOResponse getRadar(Principal principal) {
        MongoUserDTOResponse user = mongoUserService.getMongoUserDTOResponseByUsername(principal.getName());

        Radar radar = radarRepository.findById(user.id()).orElse(null);
        if (radar == null) return null;

        return radar.convertToDTOResponse(getFoodItemsForRadar(radar));
    }
}
