package com.sedisys.rates.model;

import com.sedisys.util.range.Range;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@XmlRootElement(name = "RateRecord")
public class DefaultRateRecord implements RateRecord {
	private final Set<DayOfWeek> days;
	private final Integer rate;
	private final Range<LocalTime> timeRange;

	public DefaultRateRecord(Set<DayOfWeek> days, Integer rate, Range<LocalTime> timeRange) {
		this.days = days;
		this.rate = rate;
		this.timeRange = timeRange;
	}

	@Override
	@XmlElement
	public Set<DayOfWeek> getDays() {
		return days;
	}

	@Override
	@XmlElement
	public Integer getRate() {
		return rate;
	}

	@Override
	@XmlElement
	public Range<LocalTime> getTimeRange() {
		return timeRange;
	}
}
