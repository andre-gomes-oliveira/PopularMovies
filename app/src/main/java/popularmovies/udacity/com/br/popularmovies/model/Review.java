package popularmovies.udacity.com.br.popularmovies.model;

public class Review extends Extra
{
    private final String mAuthor;
    private final String mContent;
    private final String mUrl;

    public Review(int id, String author, String content, String url)
    {
        this.mId = id;
        this.mAuthor = author;
        this.mContent = content;
        this.mUrl = url;
    }

    public int getId()
    {
        return mId;
    }

    public String getAuthor()
    {
        return mAuthor;
    }

    public String getContent()
    {
        return mContent;
    }

    public String getUrl() { return mUrl; }
}
