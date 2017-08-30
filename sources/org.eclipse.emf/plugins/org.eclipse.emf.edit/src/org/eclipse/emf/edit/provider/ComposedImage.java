/**
 * Copyright (c) 2002-2012 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.edit.provider;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This implements a wrapper that can be used to specify how a composed image should look.
 * Be careful to avoid creating a non-static subclass because a composed image is often used as a key in a long-lived map, 
 * for example, EMF's ExtendedImageRegistry,
 * and as such, anonymous or non-static nested classes will tend to cause contextual cause leaks,
 * e.g., <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=419364">419364</a>.
 */
public class ComposedImage 
{
  public static class Point
  {
    public int x;
    public int y;
    
    @Override
    public String toString()
    {
      return "(" + x + ", " + y +")";
    }
  }

  public static class Size
  {
    public int width;
    public int height;

    @Override
    public String toString()
    {
      return "(" + width+ ", " + height +")";
    }
  }

  protected List<Object> images;
  protected List<Size> imageSizes;

  /**
   * This creates an empty instance.
   */
  public ComposedImage(Collection<?> images)
  {
    this.images = new ArrayList<Object>(images);
  }

  @Override
  public boolean equals(Object that)
  {
    return that instanceof ComposedImage && ((ComposedImage)that).getImages().equals(images);
  }

  @Override
  public int hashCode()
  {
    return images.hashCode();
  }

  public List<Object> getImages()
  {
    return images;
  }

  public Size getSize(Collection<? extends Size> imageSizes)
  {
    this.imageSizes = new ArrayList<Size>(imageSizes);
    Size result = new Size();
    for (Size size : imageSizes)
    {
      result.width = Math.max(result.width, size.width);
      result.height = Math.max(result.height, size.height);
    }
    return result;
  }

  public List<Point> getDrawPoints(Size size)
  {
    List<Point> results = new ArrayList<Point>();
    for (int i = images.size(); i > 0; --i)
    {
      Point result = new Point();
      results.add(result);
    }
    return results;
  }
}
