package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.LearningTime;
import kielce.tu.weaii.telelearn.servicedata.LearningTimeData;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

import static kielce.tu.weaii.telelearn.utilities.Constants.DATE_FORMATTER_FOR_MAP_KEY;

@UtilityClass
public class LearningTimeView {
    public static Map<String, TimeVew> from(LearningTimeData data) {
        Map<String, TimeVew> map = new HashMap<>();
        map.put("default", TimeVew.form(data.getDefaultLearningTime()));
        for (LearningTime learningTime : data.getLearningTimeList()) {
            map.put(learningTime.getDate().format(DATE_FORMATTER_FOR_MAP_KEY), TimeVew.form(data.getDefaultLearningTime()));
        }
        return map;
    }
}
