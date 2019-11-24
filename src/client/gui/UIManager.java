package client.gui;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;

import javax.swing.Icon;

import utilities.infra.Log;

public class UIManager
{

// PUBLIC ENUM
	
	@SuppressWarnings("rawtypes")
	public enum Theme
	{
		
	// INSTANCES
		
		LIGHT(UIResourcesLight.class),
		DARK(UIResourcesDark.class);
		
	// PRIVATE CONSTRUCTOR
		
		private Theme(Class resources)
		{
			resources_ = resources;
		}
		
	// PUBLIC METHOD
		
		public Class resources()
		{
			return resources_;
		}
		
	// PRIVATE ATTRIBUTE
		
		private final Class resources_;
		
	};
		
// PUBLIC METHODS
	
	// Theme setter
	public static void setTheme(final Theme theme)
	{
		theme_ = theme;
	}
	
	// Resource getter from UIResources
	public static Field getResource(String resourceName)
	{
		resourceName = resourceName.toUpperCase();
		try
		{
			return theme_.resources().getField(resourceName);
		}
		catch (NoSuchFieldException e)
		{
			Log.LOG(Log.Level.ERROR, resourceName + " is not a resource !");
		}
		catch (SecurityException e)
		{
			Log.LOG(Log.Level.WARNING, "Security violation for this resource: " + resourceName);
		}
		return null;
	}
	
	// Get a string resource
	public static String getStringResource(String resourceName)
	{
		try
		{
			return (String) getResource(resourceName).get(null);
		}
		catch (IllegalArgumentException e)
		{
			Log.LOG(Log.Level.ERROR, resourceName + " is not a resource !");
		}
		catch (IllegalAccessException e)
		{
			Log.LOG(Log.Level.WARNING, "Security violation for this resource: " + resourceName);
		}
		return null;
	}
	
	// Get a font resource
	public static Font getFontResource(String resourceName)
	{
		try
		{
			return (Font) getResource(resourceName).get(null);
		}
		catch (IllegalArgumentException e)
		{
			Log.LOG(Log.Level.ERROR, resourceName + " is not a resource !");
		}
		catch (IllegalAccessException e)
		{
			Log.LOG(Log.Level.WARNING, "Security violation for this resource: " + resourceName);
		}
		return null;
	}
	
	// Get a color resource
	public static Color getColorResource(String resourceName)
	{
		try
		{
			return (Color) getResource(resourceName).get(null);
		}
		catch (IllegalArgumentException e)
		{
			Log.LOG(Log.Level.ERROR, resourceName + " is not a resource !");
		}
		catch (IllegalAccessException e)
		{
			Log.LOG(Log.Level.WARNING, "Security violation for this resource: " + resourceName);
		}
		return null;
	}
	
	// Get an icon resource
	public static Icon getIconResource(String resourceName)
	{
		try
		{
			return (Icon) getResource(resourceName).get(null);
		}
		catch (IllegalArgumentException e)
		{
			Log.LOG(Log.Level.ERROR, resourceName + " is not a resource !");
		}
		catch (IllegalAccessException e)
		{
			Log.LOG(Log.Level.WARNING, "Security violation for this resource: " + resourceName);
		}
		return null;
	}
	
// PUBLIC ATTRIBUTE	

	public static final Theme DEFAULT_THEME = Theme.DARK;
	
// PRIVATE ATTRIBUTE
	
	private static Theme theme_ = DEFAULT_THEME;
	
}
