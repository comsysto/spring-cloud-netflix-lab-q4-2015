package com.comsysto.netflix.data.historical.repository;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class HistoricalDataRepositoryImpl implements HistoricalDataRepository {

    private final Map<StorageKey, List<DataPoint>> data = Maps.newHashMap();

    @Override
    public List<DataPoint> get(DataType type, String locationId) {
        List<DataPoint> dataPoints = data.get(new StorageKey(type, locationId));
        if (dataPoints == null) {
            return Collections.emptyList();
        }
        return dataPoints;
    }

    @Override
    public DataPoint getLatest(DataType type, String locationId) {
        // latest is first in list
        List<DataPoint> dataPoints = get(type, locationId);
        if (dataPoints.isEmpty()) {
            return null;
        }
        return dataPoints.get(0);
    }

    @Override
    public void add(DataPoint dataPoint) {
        StorageKey storageKey = new StorageKey(dataPoint.getKey().getType(), dataPoint.getKey().getLocationId());
        if (!data.containsKey(storageKey)) {
            data.put(storageKey, Lists.newArrayList(dataPoint));
        } else {
            data.get(storageKey).add(0, dataPoint);
        }
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
