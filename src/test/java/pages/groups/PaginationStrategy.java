package pages.groups;

/**
 * Created by kgr on 7/24/2017.
 */
public interface PaginationStrategy {
    /**
     * Some reports have additional rows like headers (e.g. total, forecast).
     * In that case amount of rows in table is differ to items per page.
     * This strategy interface allows to solve such cases in elegant way
     * @param rows - rows per page
     * @return proper amount of rows per page
     */
    int getRowsPerPage(int rows);
}