package org.openstreetmap.josm.plugins.ods.bag.factories;

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import org.openstreetmap.josm.plugins.ods.bag.entity.NL_Address;
import org.openstreetmap.josm.plugins.ods.entities.EntityModifier;
import org.openstreetmap.josm.plugins.ods.od.OdAddressFactory;

public class NL_AddressFactoryFactory {
    static private List<OdAddressFactory> factories = new LinkedList<>();
    static private List<EntityModifier<NL_Address>> modifiers = new LinkedList<>();
   
    static {
        loadModifiers();
        loadFactories();
    }
    
    private static List<OdAddressFactory> loadFactories() {
        ServiceLoader<OdAddressFactory> serviceLoader = ServiceLoader.load(OdAddressFactory.class);
        serviceLoader.forEach(factory -> {
            modifiers.forEach(factory::addModifier);
            factories.add(factory);
        });
        return factories;
    }
    
    private static void loadModifiers() {
        @SuppressWarnings("rawtypes")
        ServiceLoader<EntityModifier> serviceLoader = ServiceLoader.load(EntityModifier.class);
        serviceLoader.forEach(m -> {
            if (m.getTargetType().equals(NL_Address.class)) {
                @SuppressWarnings("unchecked")
                EntityModifier<NL_Address> modifier = m;
                modifiers.add(modifier);
            }
        });
    }
    
    public static OdAddressFactory create() {
        return factories.get(0);
    }
}
