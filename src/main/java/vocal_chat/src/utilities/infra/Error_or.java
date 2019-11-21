package utilities.infra;

public class Error_or<T>
{

// PUBLIC

    public Error_or(final T obj)
    {
        object_ = obj;
        is_error_ = false;
        error_message_ = null;
    }

    public Error_or(final String error_msg)
    {
        is_error_ = true;
        error_message_ = error_msg;
    }
    
    public final boolean error()
    {
        return is_error_;
    }

    public final String explain()
    {
        return error_message_;
    }

    public final T get()
    {
        assert object_ != null : "Impossible to get null object from error";
        return object_;
    }

// PRIVATE

    private final boolean is_error_;
    private final String error_message_;


    private T object_ = null;
}
