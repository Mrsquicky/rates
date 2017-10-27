package com.sedisys.rates.api;

import com.sedisys.rates.model.RateRecord;
import com.sedisys.rates.service.RateRecordService;
import com.sedisys.util.range.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.core.Response;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Controller
public class RateApiImpl implements RateApi {

	private RateRecordService rateRecordService;

	@Autowired
	public RateApiImpl(RateRecordService rateRecordService){
		this.rateRecordService = rateRecordService;
	}

	@Override
	public Response getRate(String start, String end) {
		ZonedDateTime startDateTime;
		ZonedDateTime endDateTime;
		try {
			startDateTime = ZonedDateTime.parse(start);
			endDateTime = ZonedDateTime.parse(end);
		} catch (Exception e){
			return Response.status(400, "Date times must be formatted in ISO format").build();
		}
		DayOfWeek startDay = startDateTime.getDayOfWeek();
		if (!endDateTime.getDayOfWeek().equals(startDay)){
			return Response.status(400, "Date times must be from the same day").build();
		}

		Range<LocalTime> searchTimeRange = new Range<>(startDateTime.toLocalTime(), endDateTime.toLocalTime());
		Optional<RateRecord> matchingRateRecordOptional = rateRecordService.findContainingRateRecord(startDay, searchTimeRange);
		if (!matchingRateRecordOptional.isPresent()){
			return Response.ok("Unavailable").build();
		}
		return Response.ok(matchingRateRecordOptional.get().getRate()).build();
	}
}
