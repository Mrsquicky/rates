package com.sedisys.rates.service;


import com.sedisys.rates.mock.MockRateRecordRepository;
import com.sedisys.rates.model.DefaultRateRecord;
import com.sedisys.rates.model.RateRecord;
import com.sedisys.util.range.Range;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RateServiceTest {

	private MockRateRecordRepository rateRecordRepository;
	private DefaultRateRecordService rateRecordService;

	@Before
	public void setupRateRecordService(){
		rateRecordRepository = new MockRateRecordRepository();
		rateRecordService = new DefaultRateRecordService(rateRecordRepository);
	}

	private void addRateRecord(Integer rate, Range<LocalTime> timeRange, DayOfWeek...days){
		Set<DayOfWeek> daysSet = new HashSet<>(Arrays.asList(days));
		rateRecordRepository.getRateRecords().add(new DefaultRateRecord(daysSet, rate, timeRange));
	}

	private Range<LocalTime> createLocalTimeRange(int startHour, int startMinutes, int minutesDuration){
		LocalTime startTime = LocalTime.of(startHour, startMinutes);
		return new Range<>(startTime, startTime.plusMinutes(minutesDuration));
	}

	@Test
	public void testRateLookupSingleDay(){
		int rateBase = 100;

		for (int i=0;i<22;i+=2){
			Integer rate = Integer.valueOf(i * rateBase);
			Range<LocalTime> timeRange = createLocalTimeRange(i, 0,60);
			addRateRecord(rate, timeRange, DayOfWeek.MONDAY);
		}
		rateRecordService.loadRateRecords();

		for (int i=0;i<22;i++){
			Range<LocalTime> timeRange = createLocalTimeRange(i, 0, 60);
			Optional<RateRecord> rateRecordOptional = rateRecordService.findContainingRateRecord(DayOfWeek.MONDAY, timeRange);
			if (i % 2 == 1){
				Assert.assertFalse("Range " + timeRange + " should not match any records, but matched", rateRecordOptional.isPresent());
			} else{
				Assert.assertTrue(rateRecordOptional.isPresent());
				Assert.assertEquals(Integer.valueOf(i * rateBase), rateRecordOptional.get().getRate());
			}

			rateRecordOptional = rateRecordService.findContainingRateRecord(DayOfWeek.THURSDAY, timeRange);
			Assert.assertFalse(rateRecordOptional.isPresent());

			timeRange = createLocalTimeRange(i, 15, 30);
			rateRecordOptional = rateRecordService.findContainingRateRecord(DayOfWeek.MONDAY, timeRange);
			if (i % 2 == 1){
				Assert.assertFalse(rateRecordOptional.isPresent());
			} else{
				Assert.assertTrue(rateRecordOptional.isPresent());
				Assert.assertEquals(Integer.valueOf(i * rateBase), rateRecordOptional.get().getRate());
			}

			if (i > 0) {
				timeRange = createLocalTimeRange(i - 1, 30, 60);
				rateRecordOptional = rateRecordService.findContainingRateRecord(DayOfWeek.MONDAY, timeRange);
				Assert.assertFalse(rateRecordOptional.isPresent());
			}
		}
	}

	@Test
	public void testRateLookupMultipleDays(){

		Integer rate = Integer.valueOf(2000);
		Range<LocalTime> timeRange = createLocalTimeRange(4, 0,120);
		addRateRecord(rate, timeRange, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);

		rate = Integer.valueOf(4000);
		timeRange = createLocalTimeRange(14, 0,120);
		addRateRecord(rate, timeRange, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);

		rate = Integer.valueOf(1000);
		timeRange = createLocalTimeRange(3, 0,240);
		addRateRecord(rate, timeRange, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY);
		rateRecordService.loadRateRecords();

		rate = Integer.valueOf(2000);
		timeRange = createLocalTimeRange(13, 0,240);
		addRateRecord(rate, timeRange, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY);
		rateRecordService.loadRateRecords();

		rate = Integer.valueOf(3000);
		timeRange = createLocalTimeRange(19, 0,30);
		addRateRecord(rate, timeRange, DayOfWeek.TUESDAY);
		rateRecordService.loadRateRecords();

		Range<LocalTime> testTimeRange = createLocalTimeRange(5, 15, 30);

		for (DayOfWeek day : DayOfWeek.values()){
			Optional<RateRecord> rateRecordOptional = rateRecordService.findContainingRateRecord(day, testTimeRange);
			switch (day){
				case SUNDAY:
				case SATURDAY:
					Assert.assertFalse(rateRecordOptional.isPresent());
					break;
				case MONDAY:
				case WEDNESDAY:
				case FRIDAY:
					Assert.assertTrue(rateRecordOptional.isPresent());
					Assert.assertEquals(Integer.valueOf(2000), rateRecordOptional.get().getRate());
					break;
				case TUESDAY:
				case THURSDAY:
					Assert.assertTrue(rateRecordOptional.isPresent());
					Assert.assertEquals(Integer.valueOf(1000), rateRecordOptional.get().getRate());
					break;
			}
		}

		testTimeRange = createLocalTimeRange(15, 15, 30);

		for (DayOfWeek day : DayOfWeek.values()){
			Optional<RateRecord> rateRecordOptional = rateRecordService.findContainingRateRecord(day, testTimeRange);
			switch (day){
				case SUNDAY:
				case SATURDAY:
					Assert.assertFalse(rateRecordOptional.isPresent());
					break;
				case MONDAY:
				case WEDNESDAY:
				case FRIDAY:
					Assert.assertTrue(rateRecordOptional.isPresent());
					Assert.assertEquals(Integer.valueOf(4000), rateRecordOptional.get().getRate());
					break;
				case TUESDAY:
				case THURSDAY:
					Assert.assertTrue(rateRecordOptional.isPresent());
					Assert.assertEquals(Integer.valueOf(2000), rateRecordOptional.get().getRate());
					break;
			}
		}

		testTimeRange = createLocalTimeRange(5, 15, 600);

		for (DayOfWeek day : DayOfWeek.values()){
			Optional<RateRecord> rateRecordOptional = rateRecordService.findContainingRateRecord(day, testTimeRange);
			switch (day){
				case SUNDAY:
				case SATURDAY:
				case MONDAY:
				case WEDNESDAY:
				case FRIDAY:
				case THURSDAY:
				case TUESDAY:
					Assert.assertFalse(rateRecordOptional.isPresent());
					break;
			}
		}

		testTimeRange = createLocalTimeRange(19, 15, 10);

		for (DayOfWeek day : DayOfWeek.values()){
			Optional<RateRecord> rateRecordOptional = rateRecordService.findContainingRateRecord(day, testTimeRange);
			switch (day){
				case SUNDAY:
				case SATURDAY:
				case MONDAY:
				case WEDNESDAY:
				case FRIDAY:
				case THURSDAY:
					Assert.assertFalse(rateRecordOptional.isPresent());
					break;
				case TUESDAY:
					Assert.assertTrue(rateRecordOptional.isPresent());
					Assert.assertEquals(Integer.valueOf(3000), rateRecordOptional.get().getRate());
					break;
			}
		}
	}
}
