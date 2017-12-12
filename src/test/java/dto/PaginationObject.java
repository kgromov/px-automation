package dto;

import pages.groups.PaginationStrategy;

/**
 * Created by kgr on 9/20/2017.
 */
public class PaginationObject {
    public int itemsPerPage;
    public int totalItems;
    public int totalPages;
    private PaginationStrategy strategy;

    public PaginationObject(int itemsPerPage, int totalItems, int totalPages, PaginationStrategy strategy) {
        this.itemsPerPage = itemsPerPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.strategy = strategy;
    }

    public PaginationObject(int itemsPerPage, int totalItems, int totalPages) {
        this.itemsPerPage = itemsPerPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    @Override
    public String toString() {
        return "PaginationObject{" +
                "itemsPerPage=" + itemsPerPage +
                ", totalItems=" + totalItems +
                ", totalPages=" + totalPages +
                '}';
    }
}
