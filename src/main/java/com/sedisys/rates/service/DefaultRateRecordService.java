package com.sedisys.rates.service;

import com.sedisys.rates.model.RateRecord;
import com.sedisys.rates.repository.RateRecordRepository;
import com.sedisys.util.range.Range;
import com.sedisys.util.range.RangeMap;
import com.sedisys.util.range.RangeTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class DefaultRateRecordService implements RateRecordService {
	private final Map<DayOfWeek, RangeMap<LocalTime, RateRecord>> dayRateMap;

	private RateRecordRepository rateRecordRepository;

	@Autowired
	public DefaultRateRecordService(RateRecordRepository rateRecordRepository) {
		this.rateRecordRepository = rateRecordRepository;
		dayRateMap = new HashMap<>();
		for (DayOfWeek day : DayOfWeek.values()){
			dayRateMap.put(day, new RangeTreeMap<>());
		}
		loadRateRecords();
	}

	public void loadRateRecords(){
		for (RateRecord rateRecord : rateRecordRepository.getRateRecords()){
			addRateRecord(rateRecord);
		}
	}

	public Map<DayOfWeek, RangeMap<LocalTime, RateRecord>> getDayRateMap() {
		return dayRateMap;
	}

	public void addRateRecord(DayOfWeek day,  Range<LocalTime> timeRange,  RateRecord rateRecord){
		dayRateMap.get(day).addRangedReference(timeRange, rateRecord);
	}

	public void addRateRecord(DayOfWeek day,  LocalTime start, LocalTime end,  RateRecord rateRecord){
		addRateRecord(day, new Range<>(start, end), rateRecord);
	}

	public void addRateRecord(RateRecord rateRecord){
		for (DayOfWeek day : rateRecord.getDays()){
			addRateRecord(day, rateRecord.getTimeRange(), rateRecord);
		}
	}

	@Override
	public Optional<RateRecord> findContainingRateRecord(DayOfWeek day, Range<LocalTime> localTimeRange) {
		Set<RateRecord> containingRateRecords = getDayRateMap().get(day).findContainingObjects(localTimeRange);
		if (containingRateRecords.isEmpty()){
			return Optional.empty();
		}
		return Optional.of(containingRateRecords.iterator().next());
	}
}
