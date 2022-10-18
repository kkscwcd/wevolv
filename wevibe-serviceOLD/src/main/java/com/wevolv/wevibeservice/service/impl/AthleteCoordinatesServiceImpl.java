package com.wevolv.wevibeservice.service.impl;

import com.wevolv.wevibeservice.domain.model.AthleteCoordinates;
import com.wevolv.wevibeservice.domain.model.AthletesGeoLocation;
import com.wevolv.wevibeservice.domain.model.dto.AthleteCoordinatesDTO;
import com.wevolv.wevibeservice.domain.model.dto.AthleteTagFilterDto;
import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import com.wevolv.wevibeservice.integration.profile.service.ProfileService;
import com.wevolv.wevibeservice.repository.AthletesCoordinatesRepository;
import com.wevolv.wevibeservice.repository.impl.AthletesCoordinatesCustomRepositoryImpl;
import com.wevolv.wevibeservice.service.AthleteCoordinatesService;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class AthleteCoordinatesServiceImpl implements AthleteCoordinatesService {

    @Autowired
    private  AthletesCoordinatesRepository athletesCoordinatesRepository;
    @Autowired
    private  ProfileService profileService;
    @Autowired
    private AthletesCoordinatesCustomRepositoryImpl athletesCoordinatesCustomRepositoryImpl;

    public AthleteCoordinatesServiceImpl() {
    }

    @Override
    public Optional<AthleteCoordinates> saveAthletesCoordinates(AthleteCoordinatesDTO athleteCoordinatesDTO, String keycloakId) {
        Optional<AthleteCoordinates> athleteCoordinates = athletesCoordinatesRepository.findByKeycloakId(keycloakId);
        double[] coordinates=new double[2];
        if(athleteCoordinates.isPresent()){
            coordinates[0]=athleteCoordinatesDTO.getLongitude();
            coordinates[1]=athleteCoordinatesDTO.getLatitude();
            athleteCoordinates.get().getAthletesGeoLocation().setCoordinates(coordinates);
//            athleteCoordinates.get().getAthletesGeoLocation().setLongitude(athleteCoordinatesDTO.getLongitude());
//            athleteCoordinates.get().getAthletesGeoLocation().setLatitude(athleteCoordinatesDTO.getLatitude());
            athletesCoordinatesRepository.save(athleteCoordinates.get());
            return athleteCoordinates;
        } else {
            var geoLocation = AthletesGeoLocation.builder()
                    .coordinates(coordinates)
//                    .latitude(athleteCoordinatesDTO.getLatitude())
//                    .longitude(athleteCoordinatesDTO.getLongitude())
                    .build();

            var newAthleteCoordinates = AthleteCoordinates.builder()
                    .athletesGeoLocation(geoLocation)
                    .keycloakId(keycloakId)
                    .build();
            athletesCoordinatesRepository.save(newAthleteCoordinates);
            return Optional.of(newAthleteCoordinates);
        }
/*
        athleteCoordinates.ifPresentOrElse(eac -> {
            eac.getAthletesGeoLocation().setLatitude(athleteCoordinatesDTO.getLatitude());
            eac.getAthletesGeoLocation().setLongitude(athleteCoordinatesDTO.getLongitude());
            athletesCoordinatesRepository.save(eac);
        }, () -> {
            var geoLocation = AthletesGeoLocation.builder()
                    .latitude(athleteCoordinatesDTO.getLatitude())
                    .longitude(athleteCoordinatesDTO.getLongitude())
                    .build();

            var newAthleteCoordinates = AthleteCoordinates.builder()
                    .athletesGeoLocation(geoLocation)
                    .keycloakId(keycloakId)
                    .build();
            athletesCoordinatesRepository.save(newAthleteCoordinates);
        });

        return athleteCoordinates;*/
    }

    @Override
    public Optional<AthleteCoordinates> getAllAthletesCoordinates(String keycloakId) {
        return athletesCoordinatesRepository.findByKeycloakId(keycloakId);
    }

    @Override
    public Optional<List<AthleteCoordinates>> getAthletesCoordinatesNearby(AthleteCoordinatesDTO athleteCoordinatesDTO, String keycloakId) {
//        Point basePoint = new Point(athleteCoordinatesDTO.getLongitude(), athleteCoordinatesDTO.getLatitude());
//        Distance radius = new Distance(athleteCoordinatesDTO.getRadius(), Metrics.KILOMETERS);
//        Circle area = new Circle(basePoint, radius);

       // Optional<List<AthleteCoordinates>> athleteCoordinatesList = athletesCoordinatesRepository.findByAthletesGeoLocationWithin(area);
         List<AthleteCoordinates> athleteCoordinatesList = athletesCoordinatesCustomRepositoryImpl.findByAthletesGeoLocationWithin(athleteCoordinatesDTO);
        
        //athleteCoordinatesList.ifPresent(acl -> {
           if(athleteCoordinatesList!=null && !athleteCoordinatesList.isEmpty()){
            athleteCoordinatesList.forEach(up -> {
                Optional<ProfileShortInfo> psi = profileService.userShortProfileByKeycloakId(up.getKeycloakId());
                psi.ifPresent(up::setProfile);
            });
           }
       // });
        
        return Optional.of( athleteCoordinatesList);
    }

    @Override
    public List<ProfileShortInfo> filterByTags(AthleteTagFilterDto vibeTagFilterDto) {
        return profileService.filterAthletes(vibeTagFilterDto);
    }
}
