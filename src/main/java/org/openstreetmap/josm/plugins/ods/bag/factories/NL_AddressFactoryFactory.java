package org.openstreetmap.josm.plugins.ods.bag.factories;

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import org.openstreetmap.josm.plugins.ods.od.OdAddressFactory;
import org.openstreetmap.josm.plugins.ods.od.OdAddressModifier;

public class NL_AddressFactoryFactory {
    static private List<OdAddressFactory> factories = new LinkedList<>();
    static private List<OdAddressModifier<?>> modifiers = new LinkedList<>();
   
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
        ServiceLoader<OdAddressModifier> serviceLoader = ServiceLoader.load(OdAddressModifier.class);
        serviceLoader.forEach(modifier -> {
            modifiers.add(modifier);
        });
    }
    
    public static OdAddressFactory create() {
        return factories.get(0);
    }
}
