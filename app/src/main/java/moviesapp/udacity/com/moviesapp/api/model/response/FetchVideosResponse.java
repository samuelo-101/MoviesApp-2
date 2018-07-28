package moviesapp.udacity.com.moviesapp.api.model.response;

import java.util.List;

import moviesapp.udacity.com.moviesapp.api.model.Video;

public class FetchVideosResponse {

    private int id;
    private List<Video> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
}
