package me.anisekai.toshiko.services;

import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.interfaces.entities.ISeasonalSelection;
import me.anisekai.toshiko.services.data.SeasonalSelectionDataService;
import me.anisekai.toshiko.services.data.SeasonalVoterDataService;
import org.springframework.stereotype.Service;

@Service
public class ToshikoService {

    private final SeasonalSelectionDataService seasonalSelectionService;
    private final SeasonalVoterDataService     seasonalVoterService;

    public ToshikoService(SeasonalSelectionDataService seasonalSelectionService, SeasonalVoterDataService seasonalVoterService) {

        this.seasonalSelectionService = seasonalSelectionService;
        this.seasonalVoterService     = seasonalVoterService;
    }

    public ISeasonalSelection createNewSelection(String name) {

        SeasonalSelection seasonalSelection = this.seasonalSelectionService.open(name);
        this.seasonalVoterService.create(seasonalSelection);
        return this.seasonalSelectionService.fetch(seasonalSelection.getId());
    }

}
