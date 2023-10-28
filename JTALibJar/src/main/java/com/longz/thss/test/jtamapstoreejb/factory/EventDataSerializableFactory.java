package com.longz.thss.test.jtamapstoreejb.factory;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.longz.thss.test.jtamapstoreejb.entity.Event;

public class EventDataSerializableFactory implements DataSerializableFactory {
    public static final int FACTORY_ID = 11104;
    @Override
    public IdentifiedDataSerializable create(int i) {
        if (i == 1104) {
            return new Event();
        } else {
            return null;
        }
    }
}
