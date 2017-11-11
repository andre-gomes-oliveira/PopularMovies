package popularmovies.udacity.com.br.popularmovies.model;

public class Trailer extends Extra
{
    private final String mName;
    private final String mType;
    private final String mKey;

    public Trailer(int id, String name, String type, String key)
    {
        this.mId = id;
        this.mName = name;
        this.mType = type;
        this.mKey = key;
    }

    public int getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }

    public String getType()
    {
        return mType;
    }

    public String getKey() { return mKey; }
}
