/*
 * Wrapper class to make the use of output parameter more explicit and non nullable
 */
public class Out<T>
{

// PUBLIC
	public Out(T output_parameter)
	{
		parameter_ = output_parameter;
		assert parameter_ != null : "An Output paramter can't be null";
	}

	public Out(Out<T> output_parameter)
	{
		parameter_ = output_parameter.get();
		assert parameter_ != null : "An Output paramter can't be null";
	}

	public T get()
	{
		return parameter_;
	}

	public void insert(T new_parameter)
	{
		parameter_  = new_parameter;
	}

// PRIATE
	private T parameter_ = null;

}