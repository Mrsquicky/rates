package com.sedisys.util.range;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface RangeMap<T extends Comparable<? super T>, Referenced> {
	Map<Range<T>, List<Referenced>> getRangeToReferencedMap();

	void addRangedReference(Range<T> keyRange, Referenced referenced);

	void addRangedReference(Referenced referenced, Function<Referenced, Range<T>> rangeExtractor);

	Set<Range<T>> findMatchingRanges(T searchValue);

	Set<Referenced> findMatchingObjects(T searchValue);

	Set<Range<T>> findOverlappingRanges(Range<T> searchRange);

	Set<Referenced> findOverlappingObjects(Range<T> searchRange);

	Set<Range<T>> findContainingRanges(Range<T> searchRange);

	Set<Referenced> findContainingObjects(Range<T> searchRange);
}
