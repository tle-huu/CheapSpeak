import java.util.Vector;


/*
 *  Templated RingBuffer class foor inter threads communication
 *
 *  
 */
public class RingBuffer<T>
{
// PRIVATE CONST

    private final int DEFAULT_SIZE = 1024;

// PUBLIC

    public boolean push(T elem)
    {
        return buffer_.add(elem);
    }

    public T pop()
    {
        if (is_empty())
        {
            // Log.LOG(Log.Level.ERROR, "RingBuffer error: Trying to pop from an empty queue");
            return null;
        }
        T first_elem = buffer_.firstElement();
        buffer_.remove(0);
        return first_elem;
    }

    public int size()
    {
        return buffer_.size();
    }

    public boolean is_empty()
    {
        return buffer_.isEmpty();
    }

// PRIVATE

    private Vector<T> buffer_ = new Vector<T>(DEFAULT_SIZE);

}
