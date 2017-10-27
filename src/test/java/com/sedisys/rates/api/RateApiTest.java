package com.sedisys.rates.api;

import com.sedisys.rates.mock.MockRateRecordRepository;
import com.sedisys.rates.model.DefaultRateRecord;
import com.sedisys.rates.service.DefaultRateRecordService;
import com.sedisys.util.range.Range;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RateApiTest {
	private MockRateRecordRepository rateRecordRepository;
	private DefaultRateRecordService rateRecordService;
	private RateApiImpl rateApi;

	@Before
	public void setupRateRepository(){
		rateRecordRepository = new MockRateRecordRepository();
	}

	private void addRateRecord(Integer rate, Range<LocalTime> timeRange, DayOfWeek...days){
		Set<DayOfWeek> daysSet = new HashSet<>(Arrays.asList(days));
		rateRecordRepository.getRateRecords().add(new DefaultRateRecord(daysSet, rate, timeRange));
	}

	private Range<LocalTime> createLocalTimeRange(int startHour, int startMinutes, int minutesDuration){
		LocalTime startTime = LocalTime.of(startHour, startMinutes);
		return new Range<>(startTime, startTime.plusMinutes(minutesDuration));
	}

	private Range<ZonedDateTime> createZonedDateTimeRange(DayOfWeek day, int startHour, int startMinutes, int minutesDuration){
		ZonedDateTime startDateTime = ZonedDateTime.now().with(TemporalAdjusters.next(day)).withHour(startHour).withMinute(startMinutes).withSecond(0).withNano(0);
		return new Range<>(startDateTime, startDateTime.plusMinutes(minutesDuration));
	}

	private String getDateTimeLookupResult(Range<ZonedDateTime> dateTimeRange){
		return rateApi.getRate(dateTimeRange.getStart().format(DateTimeFormatter.ISO_ZONED_DATE_TIME), dateTimeRange.getEnd().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)).getEntity().toString();
	}

	private void verifyUnavailable(String result) {
		Assert.assertEquals("Unavailable", result);
	}

	@Test
	public void testRateLookupSingleDay(){
		int rateBase = 100;

		for (int i=1;i<23;i+=2){
			Integer rate = Integer.valueOf(i * rateBase);
			Range<LocalTime> timeRange = createLocalTimeRange(i, 0,60);
			addRateRecord(rate, timeRange, DayOfWeek.MONDAY);
		}
		rateRecordService = new DefaultRateRecordService(rateRecordRepository);
		rateApi = new RateApiImpl(rateRecordService);

		for (int i=1;i<23;i++){
			Range<ZonedDateTime> dateTimeRange = createZonedDateTimeRange(DayOfWeek.MONDAY, i, 0, 60);
			String result = getDateTimeLookupResult(dateTimeRange);
			if (i % 2 == 0){
				verifyUnavailable(result);
			} else{
				Assert.assertEquals(Integer.valueOf(i * rateBase).toString(), result);
			}

			dateTimeRange = createZonedDateTimeRange(DayOfWeek.THURSDAY, i, 0, 60);
			result = getDateTimeLookupResult(dateTimeRange);
			verifyUnavailable(result);

			dateTimeRange = createZonedDateTimeRange(DayOfWeek.MONDAY, i, 15, 30);
			result = getDateTimeLookupResult(dateTimeRange);
			if (i % 2 == 0){
				verifyUnavailable(result);
			} else{
				Assert.assertEquals(Integer.valueOf(i * rateBase).toString(), result);
			}

			if (i > 0) {
				dateTimeRange = createZonedDateTimeRange(DayOfWeek.MONDAY, i - 1, 30, 60);
				result = getDateTimeLookupResult(dateTimeRange);
				verifyUnavailable(result);
			}
		}
	}
}
