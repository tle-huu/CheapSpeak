package utilities;

import java.util.Vector;

/*
 *  Templated RingBuffer class for inter threads communication
 *  
 *  A vector is used as the underlying structure to ensure thread safety
 */
public class FixedVector<T>
{

// PUBLIC METHODS

    public boolean push(T elem)
    {
        return buffer_.add(elem);
    }

    public T pop()
    {
        if (isEmpty())
        {
            return null;
        }
        T firstElem = buffer_.firstElement();
        buffer_.remove(0);
        return firstElem;
    }

    public int size()
    {
        return buffer_.size();
    }

    public boolean isEmpty()
    {
        return buffer_.isEmpty();
    }

// PRIVATE ATTRIBUTES
    
    private final int DEFAULT_SIZE = 1024;

    private Vector<T> buffer_ = new Vector<T>(DEFAULT_SIZE);

}
