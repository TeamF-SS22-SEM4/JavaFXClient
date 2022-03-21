package at.fhv.ec.javafxclient.application.impl;

import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;

import java.util.List;

public class ProductSearchServiceImpl implements ProductSearchService {
    @Override
    public List<ProductOverviewDTO> fullTextSearch(String query) {
        return List.of(
                ProductOverviewDTO.builder().withName("Album 1").withArtistName("Artist 1").withReleaseYear("1980").build(),
                ProductOverviewDTO.builder().withName("Album 2").withArtistName("Artist 2").withReleaseYear("1990").build(),
                ProductOverviewDTO.builder().withName("Album 3").withArtistName("Artist 3").withReleaseYear("1970").build(),
                ProductOverviewDTO.builder().withName("Album 4").withArtistName("Artist 4").withReleaseYear("1960").build(),
                ProductOverviewDTO.builder().withName("Album 5").withArtistName("Artist 5").withReleaseYear("2005").build(),
                ProductOverviewDTO.builder().withName("Album 6").withArtistName("Artist 6").withReleaseYear("2020").build()
        );
    }
}
