package com.sedisys.rates.service;

import com.sedisys.rates.model.RateRecord;
import com.sedisys.util.range.Range;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

public interface RateRecordService {
	Optional<RateRecord> findContainingRateRecord(DayOfWeek day,  Range<LocalTime> localTimeRange);
}
