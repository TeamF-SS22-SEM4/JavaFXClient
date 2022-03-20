package at.fhv.ec.javafxclient.application.api;

import at.fhv.ec.javafxclient.application.dto.ProductOverviewDTO;

import java.util.List;

public interface ProductSearchService {

    List<ProductOverviewDTO> fullTextSearch(String query);
}
