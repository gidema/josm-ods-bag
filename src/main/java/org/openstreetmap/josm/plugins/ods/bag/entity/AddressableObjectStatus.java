package org.openstreetmap.josm.plugins.ods.bag.entity;

/**
 * The addressable object status doesn't exit in the BAG definition.
 * We need this status to be able to decide what to do with address nodes in new/planned/demolished/withdrawn
 * building units or places.
 * 
 * @author gertjan
 *
 */
public enum AddressableObjectStatus {
    UNKNOWN, PLANNED, CONSTRUCTION, IN_USE, IN_USE_NOT_MEASURED, NOT_CARRIED_THROUGH, REMOVAL_DUE, REMOVED, RECONSTRUCTION, INADVERTENTLY_CREATED, ASSIGNED, WITHDRAWN;

}
