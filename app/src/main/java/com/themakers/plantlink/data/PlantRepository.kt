package com.themakers.plantlink.data

import com.themakers.plantlink.SettingsPage.providedPlants

class PlantRepository {
    /**
     * Retrieves the list of plants.
     * Currently, returns local preset data ([providedPlants]).
     * Designed to be easily updated in the future to fetch from the USDA API or a local database cache.
     */
    fun getPlants(): List<SettingsDropDownPlant> {
        return providedPlants
    }
}
