package moviesapp.udacity.com.moviesapp.api.model.response;

import java.util.List;

import moviesapp.udacity.com.moviesapp.api.model.Review;

public class FetchMovieReviewsResponse {

    private long id;
    private int page;
    List<Review> results;
    private int total_pages;
    private int total_results;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }
}
