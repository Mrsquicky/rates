package com.sedisys.rates.mock;

import com.sedisys.rates.model.RateRecord;
import com.sedisys.rates.repository.RateRecordRepository;

import java.util.HashSet;
import java.util.Set;

public class MockRateRecordRepository implements RateRecordRepository {
	private Set<RateRecord> rateRecords;

	public MockRateRecordRepository() {
		rateRecords = new HashSet<>();
	}

	@Override
	public Set<RateRecord> getRateRecords() {
		return rateRecords;
	}

	public void setRateRecords(Set<RateRecord> rateRecords) {
		this.rateRecords = rateRecords;
	}
}
