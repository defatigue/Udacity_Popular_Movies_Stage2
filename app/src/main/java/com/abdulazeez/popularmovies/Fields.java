package com.abdulazeez.popularmovies;

/**
 * Created by ABDULAZEEZ on 9/8/2015.
 */
public class Fields {

    private String id;
    private String backdrop;
    private String original_title;
    private String overview;
    private String vote_average;
    private String release_date;


    public Fields() {
        super();
    }

    public Fields(String id) {
        super();
        this.id = id;
    }

    public Fields(String id, String original_title, String overview, String vote_average, String release_date ) {
        super();
        this.id = id;
        this.original_title = original_title;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    public Fields(String id, String backdrop, String original_title, String overview, String vote_average, String release_date ) {
        super();
        this.id = id;
        this.backdrop = backdrop;
        this.original_title = original_title;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public void setOriginalTitle(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    public String getVoteAverage() {
        return vote_average;
    }

    public void setVoteAverage(String vote_average) {
        this.vote_average = vote_average;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Fields other = (Fields) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Fields [id="+id+", backdrop="+backdrop+", original_title="
                +original_title+", overview="+overview+", release_date="
                +release_date+", vote_average="+vote_average+"]";
    }
}

