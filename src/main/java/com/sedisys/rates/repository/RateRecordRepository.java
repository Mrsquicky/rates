package com.sedisys.rates.repository;

import com.sedisys.rates.model.RateRecord;

import java.util.Set;

public interface RateRecordRepository {
	Set<RateRecord> getRateRecords();
}
