package popularmovies.udacity.com.br.popularmovies.model;

public class Trailer extends Extra
{
    private final String mName;
    private final String mType;
    private final String mKey;

    public Trailer(String id, String name, String type, String key)
    {
        this.mId = id;
        this.mName = name;
        this.mType = type;
        this.mKey = key;
    }

    public String getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }

    public String getKey() { return mKey; }
}
