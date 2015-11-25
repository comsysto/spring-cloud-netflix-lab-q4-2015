package com.comsysto.netflix.data.current.repository;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataType;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class CurrentDataRepositoryImpl implements CurrentDataRepository {

    private final Map<StorageKey, DataPoint> data = new HashMap<>();

    @Override
    public DataPoint get(DataType type, String locationId) {
        return data.get(new StorageKey(type, locationId));
    }

    @Override
    public List<DataPoint> fetchAll() {
        return Lists.newArrayList(data.values());
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
