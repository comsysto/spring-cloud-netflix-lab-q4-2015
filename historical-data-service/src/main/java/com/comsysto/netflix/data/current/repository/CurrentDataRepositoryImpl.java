package com.comsysto.netflix.data.current.repository;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataType;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class CurrentDataRepositoryImpl implements CurrentDataRepository {

    private final Map<StorageKey, DataPoint> data;
    private final int outdatedThresholdInSeconds;
    private final Predicate<DataPoint> isValidPredicate;

    public CurrentDataRepositoryImpl() {
        this.data = new HashMap<>();
        this.outdatedThresholdInSeconds = 10000;
        this.isValidPredicate = dataPoint -> dataPoint != null && !isOutdated(dataPoint.getKey().getTimestamp());
    }

    @Override
    public int getOutdatedThresholdInSeconds() {
        return outdatedThresholdInSeconds;
    }

    @Override
    public DataPoint get(DataType type, String locationId) {
        DataPoint dataPoint = data.get(new StorageKey(type, locationId));
        if (isValidPredicate.test(dataPoint)) {
            return dataPoint;
        } else {
            return null;
        }
    }

    @Override
    public List<DataPoint> fetchAll() {
        return data.values().stream().filter(isValidPredicate).collect(Collectors.toList());
    }

    private boolean isOutdated(long timestamp) {
        long oldestValidTimestamp = System.currentTimeMillis() - (outdatedThresholdInSeconds * 1000);
        return oldestValidTimestamp > timestamp;
    }

    @Override
    public void put(DataPoint dataPoint) {
        StorageKey storageKey = new StorageKey(dataPoint.getKey().getType(), dataPoint.getKey().getLocationId());
        data.put(storageKey, dataPoint);
    }

    private static final class StorageKey {
        private final String locationId;
        private final DataType type;

        public StorageKey(DataType type, String locationId) {
            this.type = type;
            this.locationId = locationId;
        }

        public String getLocationId() {
            return locationId;
        }

        public DataType getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StorageKey that = (StorageKey) o;
            return Objects.equals(locationId, that.locationId) &&
                    Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(locationId, type);
        }
    }
}
