package com.kdpark.sickdan.service.query;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Meal;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.domain.MealPhoto;
import com.kdpark.sickdan.repository.DailyRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyQueryService {

    private final DailyRepository dailyRepository;

    public List<MonthDailyDto> getMonthData(Long memberId, String yyyymm) {
        List<Daily> monthData = dailyRepository.findOneMonth(memberId, yyyymm);

        return monthData.stream()
                .map(MonthDailyDto::new)
                .collect(Collectors.toList());
    }

    public DayDailyDto getDayData(Long memberId, String yyyymmdd) {
        Daily.DailyId dailyId = new Daily.DailyId(memberId, yyyymmdd);
        Daily dayData = dailyRepository.findById(dailyId);

        if (dayData == null) {
            dayData = Daily.builder()
                    .id(dailyId)
                    .build();
        }

        return new DayDailyDto(dayData);
    }

    @Data
    static public class MonthDailyDto {
        private String date;
        private String memo;
        private int walkCount;
        private float bodyWeight;

        public MonthDailyDto(Daily daily) {
            this.date = daily.getId().getDate();
            this.memo = daily.getMemo();
            this.walkCount = daily.getWalkCount();
            this.bodyWeight = daily.getBodyWeight();
        }
    }

    @Data
    static public class DayDailyDto {
        private String date;
        private String memo;
        private int walkCount;
        private float bodyWeight;
        private List<MealDto> meals;

        public DayDailyDto(Daily daily) {
            this.date = daily.getId().getDate();
            this.memo = daily.getMemo();
            this.walkCount = daily.getWalkCount();
            this.bodyWeight = daily.getBodyWeight();
            this.meals = daily.getMeals().stream()
                    .map(MealDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class MealDto {
        private Long id;
        private String description;
        private MealCategory category;
        private Long prevMeal;
        private List<MealPhotoDto> photos;

        public MealDto(Meal meal) {
            this.id = meal.getId();
            this.description = meal.getDescription();
            this.category = meal.getCategory();
            this.prevMeal = meal.getPrevMeal();
            this.photos = meal.getPhotos().stream()
                    .map(MealPhotoDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class MealPhotoDto {
        private String fileOriginName;
        private String fileName;

        public MealPhotoDto(MealPhoto photo) {
            this.fileOriginName = photo.getFileOriginName();
            this.fileName = photo.getFileName();
        }
    }
}
