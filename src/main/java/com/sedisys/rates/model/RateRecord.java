package com.sedisys.rates.model;

import com.sedisys.util.range.Range;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public interface RateRecord {
	Set<DayOfWeek> getDays();
	Integer getRate();
	Range<LocalTime> getTimeRange();
}
