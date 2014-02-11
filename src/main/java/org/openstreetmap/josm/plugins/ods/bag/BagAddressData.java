package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;

/**
 * BAG specific address data.
 * 
 * BAG housenumbers consist of up to 3 parts:
 * huisnummer: the numeric part (digits only)
 * huisletter: one optional letter (no digit allowed)
 * huisnummertoevoeging: 0 to 4 alphanumeric characters
 *  
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface BagAddressData extends Address {
    
    public void setHuisnummer(Integer huisnummer);
    public Integer getHuisnummer();

    public void setHuisletter(String huisletter);
    public String getHuisLetter();

    public void setHuisnummerToevoeging(String toevoeging);
    public String getHuisNummerToevoeging();
}