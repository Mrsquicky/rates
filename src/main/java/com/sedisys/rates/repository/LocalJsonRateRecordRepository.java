package com.sedisys.rates.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedisys.rates.model.DefaultRateRecord;
import com.sedisys.rates.model.RateRecord;
import com.sedisys.util.range.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class LocalJsonRateRecordRepository implements RateRecordRepository {

	@Value("${jsonFilePath}")
	private String jsonFilePath;

	@Override
	public Set<RateRecord> getRateRecords() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			SerializedRates records;
			Path dataPath = Paths.get(jsonFilePath);
			if (Files.exists(dataPath)){
				records = mapper.readValue(Files.newBufferedReader(dataPath), SerializedRates.class);
			} else {
				records = mapper.readValue(getClass().getClassLoader().getResourceAsStream(jsonFilePath), SerializedRates.class);
			}
			return records.getRates().stream().map(SerializedRateRecord::toRateRecord).collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static class SerializedRates{
		List<SerializedRateRecord> rates;

		public List<SerializedRateRecord> getRates() {
			return rates;
		}

		public void setRates(List<SerializedRateRecord> rates) {
			this.rates = rates;
		}
	}
	protected static class SerializedRateRecord{
		String days;
		String times;
		Integer price;

		public String getDays() {
			return days;
		}

		public void setDays(String days) {
			this.days = days;
		}

		public String getTimes() {
			return times;
		}

		public void setTimes(String times) {
			this.times = times;
		}

		public Integer getPrice() {
			return price;
		}

		public void setPrice(Integer price) {
			this.price = price;
		}

		private DayOfWeek getDayOfWeek(String day){
			switch (day.toLowerCase()){
				case "sun":
					return  DayOfWeek.SUNDAY;
				case "mon":
					return  DayOfWeek.MONDAY;
				case "tues":
					return  DayOfWeek.TUESDAY;
				case "wed":
					return  DayOfWeek.WEDNESDAY;
				case "thurs":
					return  DayOfWeek.THURSDAY;
				case "fri":
					return  DayOfWeek.FRIDAY;
				case "sat":
					return  DayOfWeek.SATURDAY;
				default:
					throw new IllegalArgumentException(day + " is not a valid day");
			}
		}

		private Set<DayOfWeek> getDaysAsEnumSet(){
			return Arrays.stream(getDays().split(",")).map(day -> getDayOfWeek(day)).collect(Collectors.toSet());
		}

		private Range<LocalTime> getTimesAsRange(){
			String[] pieces = getTimes().split("-");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
			LocalTime start = LocalTime.parse(pieces[0], formatter);
			LocalTime end = LocalTime.parse(pieces[1], formatter);

			return new Range<>(start, end);
		}

		public RateRecord toRateRecord(){
			return new DefaultRateRecord(getDaysAsEnumSet(), getPrice(), getTimesAsRange());
		}
	}
}
