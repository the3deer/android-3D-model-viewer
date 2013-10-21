package org.andresoviedo.dddmodel.impl1;

import java.util.EventObject;

public class LoadingUpdateEvent extends EventObject
{
  public static final int LOADING_MESSAGE = 0;
  public static final int TOAST_MESSAGE = 1;
  private static final long serialVersionUID = -577150429963292057L;
  private String message;
  private int type;

  public LoadingUpdateEvent(String paramString, int paramInt)
  {
    super(paramString);
    this.message = paramString;
    this.type = paramInt;
  }

  public String getMessage()
  {
    return this.message;
  }

  public int getType()
  {
    return this.type;
  }
}

/* Location:           C:\Users\Andres Oviedo\Downloads\android\3D Model Viewer_unzipped_undexed\classes_dex2jar.jar
 * Qualified Name:     net.mcmiracom.modelviewer.LoadingUpdateEvent
 * JD-Core Version:    0.6.2
 */