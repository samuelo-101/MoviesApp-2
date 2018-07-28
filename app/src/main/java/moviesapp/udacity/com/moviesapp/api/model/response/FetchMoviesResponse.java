package moviesapp.udacity.com.moviesapp.api.model.response;

import java.util.List;

import moviesapp.udacity.com.moviesapp.api.model.Movie;

public class FetchMoviesResponse {

    private int page;
    private List<Movie> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
