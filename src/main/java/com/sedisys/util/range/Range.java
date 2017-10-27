package com.sedisys.util.range;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sedisys.util.ComparableUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Optional;

@XmlRootElement
public class Range<T extends Comparable<? super T>> implements Comparable<Range<T>>{
    private T start;
    private T end;

    public Range(T start, T end){
        this.start = start;
        this.end = end;
    }

	@Override
	public int compareTo(Range<T> otherRange) {
		return getStart().compareTo(otherRange.getStart());
	}

	public enum CollisionType{
        NONE,
        CONTAINS,
        CONTAINED,
        EQUALS,
        OVERLAPS_START,
        OVERLAPS_START_MATCHES_END,
        OVERLAPS_END,
        MATCHES_START_OVERLAPS_END,
        MATCHES_START_ENDS_INSIDE,
        STARTS_INSIDE_MATCHES_END
    }

    @XmlElement
    public T getStart() {
        return start;
    }

    public void setStart(T start) {
        this.start = start;
    }

    @XmlElement
    public T getEnd() {
        return end;
    }

    public void setEnd(T end) {
        this.end = end;
    }

    @JsonIgnore
    public boolean isEmpty(){
        return getStart()==null && getEnd() == null;
    }

    @JsonIgnore
    public boolean isFull(){
        return getStart()!=null && getEnd()!=null;
    }

    public boolean contains(T point){
        return getStart().compareTo(point)<=0 && getEnd().compareTo(point)>=0;
    }

    public boolean contains(Range<T> checkRange){
        return getStart().compareTo(checkRange.getStart())<=0 && getEnd().compareTo(checkRange.getEnd())>=0;
    }

    public boolean overlaps(Range<T> checkRange){
        return !(getStart().compareTo(checkRange.getEnd())>0 || getEnd().compareTo(checkRange.getStart())<0);
    }

    public CollisionType getCollisionType(Range<T> checkRange){
        if (!overlaps(checkRange)){
            return CollisionType.NONE;
        }
        int startComparison = getStart().compareTo(checkRange.getStart());
        int endComparision = getEnd().compareTo(checkRange.getEnd());
        if (startComparison<0){
            if (endComparision<0){
                return CollisionType.OVERLAPS_START;
            }
            if (endComparision==0){
                return CollisionType.OVERLAPS_START_MATCHES_END;
            }
            return CollisionType.CONTAINS;
        }
        if (startComparison==0){
            if (endComparision<0){
                return CollisionType.MATCHES_START_ENDS_INSIDE;
            }
            if (endComparision==0){
                return CollisionType.EQUALS;
            }
            return CollisionType.MATCHES_START_OVERLAPS_END;
        }
        if (endComparision<0){
            return CollisionType.CONTAINED;
        }
        if (endComparision==0){
            return CollisionType.STARTS_INSIDE_MATCHES_END;
        }
        return CollisionType.OVERLAPS_END;
    }

    public Optional<Range<T>> getOverlappingRange(Range<T> toCheckRange){
        if (!overlaps(toCheckRange)){
            return Optional.empty();
        }
        T latestStart = ComparableUtils.min(getStart(), toCheckRange.getStart());
        T earliestEnd = ComparableUtils.max(getEnd(), toCheckRange.getEnd());
        return Optional.of(new Range<T>(latestStart, earliestEnd));
    }

    public Optional<Range<T>> getOverlappingRange(Range<T>... toCheckRanges){
        Range<T> overlappingRange = new Range<T>(getStart(), getEnd());
        for (Range<T> toCheckRange : toCheckRanges){
            Optional<Range<T>> overlappingRangeOptional = overlappingRange.getOverlappingRange(toCheckRange);
            if (overlappingRangeOptional.isPresent()){
                overlappingRange = overlappingRangeOptional.get();
            }
            return Optional.empty();
        }
        return Optional.of(overlappingRange);
    }

    public boolean equals(Object toCheck){
        if (!(toCheck instanceof Range)){
            return false;
        }
        Range toCheckRange = (Range) toCheck;
        return getStart().equals(toCheckRange.getStart()) && getEnd().equals(toCheckRange.getEnd());
    }

    @Override
    public String toString(){
    	return getStart().toString() + " - " + getEnd().toString();
    }
}
