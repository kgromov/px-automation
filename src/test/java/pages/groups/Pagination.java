package pages.groups;

/**
 * Created by kgr on 5/26/2017.
 */
public interface Pagination {
    int MAX_ROWS_PER_PAGE = 100;
    int MAX_ROWS_LIMIT = 200;

    Pagination checkPagination(PaginationStrategy strategy, int totalRowsCount);

    Pagination checkPagination(String parentContainer, PaginationStrategy strategy, int totalRowsCount);

    Pagination checkPaginationBigData(PaginationStrategy strategy, int totalRowsCount);

    void setItemPerPage(int itemsPerPage);

    int getActiveItemsPerPage();

    int getLastPage();
}
