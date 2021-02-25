/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2021, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.clustering.marshalling.protostream.util;

import java.io.IOException;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Supplier;

import org.infinispan.protostream.impl.WireFormat;
import org.wildfly.clustering.marshalling.protostream.ProtoStreamReader;

/**
 * Marshaller for a {@link Map}.
 * @author Paul Ferraro
 */
public class MapMarshaller<T extends Map<Object, Object>> extends AbstractMapMarshaller<T> {

    private final Supplier<T> factory;

    @SuppressWarnings("unchecked")
    public MapMarshaller(Supplier<T> factory) {
        super((Class<T>) factory.get().getClass());
        this.factory = factory;
    }

    @Override
    public T readFrom(ProtoStreamReader reader) throws IOException {
        T map = this.factory.get();
        boolean reading = true;
        while (reading) {
            int tag = reader.readTag();
            int index = WireFormat.getTagFieldNumber(tag);
            switch (index) {
                case ENTRY_INDEX:
                    Map.Entry<Object, Object> entry = reader.readObject(SimpleEntry.class);
                    map.put(entry.getKey(), entry.getValue());
                    break;
                default:
                    reading = (tag != 0) && reader.skipField(tag);
            }
        }
        return map;
    }
}
